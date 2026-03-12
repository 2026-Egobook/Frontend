package com.egobook.app.ui.diary.viewmodel

import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.diary.mapper.DiaryEntityMapper
import com.egobook.app.ui.diary.model.ToastMessage
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _contentState = MutableStateFlow(ContentState())
    val contentState = _contentState.asStateFlow()
    
    // 저장 성공 여부 및 토스트 메시지를 전달하는 이벤트 Flow (일회성 이벤트)
    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult = _saveResult.asSharedFlow()
    
    // 수정 모드인지 확인 (diaryId가 -1이 아니면 수정 모드)
    private val diaryId: Long = savedStateHandle.get<Long>("diaryId") ?: -1L
    private val isEditMode: Boolean get() = diaryId != -1L

    init {
        setupDate()
        if (isEditMode) {
            loadDiaryForEdit()
        }
    }

    fun setupDate() {
        // Navigation argument에서 날짜 받기(DiaryFragment에서 넘어옴)
        val dateString: String? = savedStateHandle.get<String>("selectedDate")
        if (dateString != null) {
            try {
                _selectedDate.value = LocalDate.parse(dateString)
            } catch (e: Exception) {
                // 파싱 실패 시 현재 날짜 사용
                _selectedDate.value = LocalDate.now()
            }
        }
    }
    
    /**
     * 수정 모드: 기존 일기 데이터 로드
     */
    private fun loadDiaryForEdit() {
        viewModelScope.launch {
            // 로딩 상태 설정
            _contentState.value = _contentState.value.copy(diaryLoadState = UiState.Loading)

            diaryUseCases.getDiary(diaryId)
                .onSuccess { diary ->
                    if (diary != null) {
                        // 기존 일기 데이터로 UI 상태 업데이트
                        _selectedDate.value = diary.date

                        // Domain DiaryType을 UI displayType으로 변환
                        val displayTypes = diary.types.map { it.displayType }.toSet()

                        _contentState.value = _contentState.value.copy(
                            content = diary.content,
                            selectedTypes = displayTypes,
                            selectedEmotionLevel = diary.emotionLevel ?: 3,
                            charCount = diary.content.length,
                            isSaveButtonEnabled = true,
                            diaryLoadState = UiState.Success(Unit)
                        )
                    } else {
                        _contentState.value = _contentState.value.copy(
                            diaryLoadState = UiState.Failure("일기를 찾을 수 없습니다.")
                        )
                    }
                }
                .onFailure { error ->
                    // 로드 실패 시 에러 상태 설정
                    _contentState.value = _contentState.value.copy(
                        diaryLoadState = UiState.Failure(error.message)
                    )
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

            if (isEditMode) {
                // 수정 모드 처리
                saveEditMode(state)
            } else {
                // 생성 모드 처리
                saveCreateMode(state)
            }
        }
    }

    /**
     * 수정 모드 저장 처리
     */
    private suspend fun saveEditMode(state: ContentState) {
        _saveResult.emit(SaveResult.Loading)

        val emotionLevel = if (state.selectedTypes.contains("감정")) {
            state.selectedEmotionLevel
        } else {
            null
        }
        val now = LocalDateTime.now()

        val updatedDiary = DiaryEntityMapper.createUpdatedDiary(
            diaryId = diaryId,
            selectedTypes = state.selectedTypes,
            content = state.content,
            emotionLevel = emotionLevel,
            writtenAt = now
        )

        diaryUseCases.updateDiary(
            diaryId = diaryId,
            diary = updatedDiary
        ).onSuccess {
            _saveResult.emit(SaveResult.Success(emptyList()))
        }.onFailure { error ->
            _saveResult.emit(SaveResult.Error(error.message))
        }
    }

    /**
     * 생성 모드 저장 처리 (토스트 메시지 포함)
     */
    private suspend fun saveCreateMode(state: ContentState) {
        val now = LocalDateTime.now()

        _saveResult.emit(SaveResult.Loading)

        val newDiary = DiaryEntityMapper.createNewDiary(
            selectedTypes = state.selectedTypes,
            content = state.content,
            emotionLevel = state.selectedEmotionLevel,
            date = _selectedDate.value,
            writtenAt = now
        )

        diaryUseCases.addDiary(newDiary)
            .onSuccess { rewards ->
                val messages = DiaryEntityMapper.createToastMessages(rewards)
                _saveResult.emit(SaveResult.Success(messages))
            }.onFailure { error ->
                _saveResult.emit(SaveResult.Error(error.message))
            }
    }

    /**
     * 저장 버튼 활성화 조건 체크
     * 조건: 1) 하나 이상의 일기 타입 선택 && 2) 텍스트가 조금이라도 있음
     */
    private fun isSaveButtonEnabled(selectedTypes: Set<String>, content: String): Boolean {
        return selectedTypes.isNotEmpty() && content.isNotBlank()
    }

    sealed class SaveResult {
        object Loading: SaveResult()
        data class Success(val toastMessages: List<ToastMessage>) : SaveResult()
        data class Error(val message: String?) : SaveResult()
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
        val isSaveButtonEnabled: Boolean = false, // 저장 버튼 활성화 유무
        val diaryLoadState: UiState<Unit> = UiState.Idle // 수정 모드 데이터 로드 상태
    )

}