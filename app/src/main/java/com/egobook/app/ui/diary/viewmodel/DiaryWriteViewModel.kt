package com.egobook.app.ui.diary.viewmodel

import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                _contentState.value = _contentState.value.copy(
                    content = event.value,
                    charCount = event.value.length
                )
            }
            is ContentEvent.ChangeContentFocus -> {
                _contentState.value = _contentState.value.copy(
                    isHintVisible = !event.focusState.isFocused && _contentState.value.content.isBlank()
                )
            }
            is ContentEvent.ToggleDiaryType -> {
                // TODO: 일기 유형 토글 처리
            }
            is ContentEvent.SaveDiary -> {
                // TODO: 일기 저장 처리
            }
        }
    }

    sealed class ContentEvent {

        data class ToggleDiaryType(val value: String): ContentEvent()
        data class EnteredContent(val value: String): ContentEvent()
        data class ChangeContentFocus(val focusState: FocusState): ContentEvent()
        data class SaveDiary(val content: String): ContentEvent()

    }

    data class ContentState(
        val selectedType: String? = null,
        val content: String = "",
        val hint: String = "오늘 하루는 어땠나요?",
        val isHintVisible: Boolean = false,
        val charCount: Int = 0,
        val maxCharCount: Int = 400
    )

}