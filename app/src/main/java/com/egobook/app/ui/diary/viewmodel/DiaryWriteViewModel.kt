package com.egobook.app.ui.diary.viewmodel

import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.diary.mapper.DiaryMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _contentState = MutableStateFlow(ContentState())
    val contentState = _contentState.asStateFlow()
    
    // 저장 성공 여부를 전달하는 이벤트 Flow (일회성 이벤트)
    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    init {
        setupDate()
    }

    fun setupDate() {
        // Navigation argument에서 날짜 받기
        val dateString: String? = savedStateHandle.get<String>("selectedDate")
        if (dateString != null) {
            try {
                _selectedDate.value = LocalDateTime.parse(dateString)
            } catch (e: Exception) {
                // 파싱 실패 시 현재 날짜 사용
                _selectedDate.value = LocalDateTime.now()
            }
        }
    }

    fun onEvent(event: ContentEvent) {
        when (event) {
            is ContentEvent.EnteredContent -> {
                val newContent = event.value
                val newCharCount = newContent.length
                val currentState = _contentState.value
                
                _contentState.value = currentState.copy(
                    content = newContent,
                    charCount = newCharCount,
                    // 저장 버튼 활성화 상태 업데이트: 타입 선택 여부 + 텍스트 입력 여부 체크
                    isSaveButtonEnabled = isSaveButtonEnabled(
                        selectedTypes = currentState.selectedTypes,
                        content = newContent
                    )
                )
            }
            is ContentEvent.ChangeContentFocus -> {
                _contentState.value = _contentState.value.copy(
                    isHintVisible = !event.focusState.isFocused && _contentState.value.content.isBlank()
                )
            }
            is ContentEvent.ToggleDiaryType -> {
                val currentState = _contentState.value
                val currentTypes = currentState.selectedTypes.toMutableSet()
                if (currentTypes.contains(event.value)) {
                    currentTypes.remove(event.value)
                } else {
                    currentTypes.add(event.value)
                }
                
                _contentState.value = currentState.copy(
                    selectedTypes = currentTypes,
                    // 저장 버튼 활성화 상태 업데이트: 타입 변경 시마다 조건 재검사
                    isSaveButtonEnabled = isSaveButtonEnabled(
                        selectedTypes = currentTypes,
                        content = currentState.content
                    )
                )
            }
            is ContentEvent.SelectEmotionLevel -> {
                _contentState.value = _contentState.value.copy(
                    selectedEmotionLevel = event.level
                )
            }
            is ContentEvent.SaveDiary -> {
                saveDiary()
            }
        }
    }
    
    /**
     * 일기 저장 처리
     */
    private fun saveDiary() {
        viewModelScope.launch {
            val state = _contentState.value
            
            // UI displayType을 Domain DiaryType으로 변환
            val diaryTypes = DiaryMapper.uiDisplayTypesToDomain(state.selectedTypes)
            
            // 감정 타입이 선택되지 않았으면 emotionLevel은 null
            val emotionLevel = if (state.selectedTypes.contains("감정")) {
                state.selectedEmotionLevel
            } else {
                null
            }
            
            // UseCase를 통해 일기 저장
            // createdAt: 목록에서 선택한 날짜
            val result = diaryUseCases.addDiary(
                content = state.content,
                types = diaryTypes,
                emotionLevel = emotionLevel,
                createdAt = _selectedDate.value // savedStateHandle로 받은 선택된 날짜
            )
            
            // 저장 결과 전달
            result.onSuccess {
                _saveSuccess.emit(true) // 저장 성공
            }.onFailure {
                _saveSuccess.emit(false) // 저장 실패
            }
        }
    }
    
    /**
     * 저장 버튼 활성화 조건 체크
     * 조건: 1) 하나 이상의 일기 타입 선택 && 2) 텍스트가 조금이라도 있음
     */
    private fun isSaveButtonEnabled(selectedTypes: Set<String>, content: String): Boolean {
        return selectedTypes.isNotEmpty() && content.isNotBlank()
    }

    sealed class ContentEvent {
        data class ToggleDiaryType(val value: String): ContentEvent()
        data class EnteredContent(val value: String): ContentEvent()
        data class ChangeContentFocus(val focusState: FocusState): ContentEvent()
        data class SelectEmotionLevel(val level: Int): ContentEvent()
        data object SaveDiary: ContentEvent()
    }

    data class ContentState(
        val selectedTypes: Set<String> = emptySet(),
        val selectedEmotionLevel: Int = 3, // 디폴트는 NORMAL (3)
        val content: String = "",
        val hint: String = "오늘 하루는 어땠나요?",
        val isHintVisible: Boolean = false,
        val charCount: Int = 0,
        val maxCharCount: Int = 400,
        val isSaveButtonEnabled: Boolean = false // 저장 버튼 활성화 유무
    )

}