package com.egobook.app.ui.diary.viewmodel

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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val selectedDate = _selectedDate.asStateFlow()

    init {
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

}