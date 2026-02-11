package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryCheckViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _diaryState = MutableStateFlow<UiState<Diary>>(UiState.Loading)
    val diaryState = _diaryState.asStateFlow()

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess = _deleteSuccess.asStateFlow()

    private val diaryId: Long = savedStateHandle.get<Long>("diaryId") ?: -1L

    init {
        if (diaryId != -1L) {
            getDiary(diaryId)
        } else {
            _diaryState.value = UiState.Failure("잘못된 일기 ID입니다.")
        }
    }

    private fun getDiary(id: Long) {
        viewModelScope.launch {
            _diaryState.value = UiState.Loading
            
            diaryUseCases.getDiary(id)
                .onSuccess { fetchedDiary ->
                    _diaryState.value = UiState.Success(fetchedDiary)
                }
                .onFailure { exception ->
                    _diaryState.value = UiState.Failure(exception.message)
                }
        }
    }
    
    /**
     * 일기 데이터 새로고침 (수정 후 돌아왔을 때 사용)
     */
    fun refreshDiary() {
        if (diaryId != -1L) {
            getDiary(diaryId)
        }
    }

    fun deleteDiary() {
        viewModelScope.launch {
            diaryUseCases.deleteDiary(diaryId)
                .onSuccess {
                    _deleteSuccess.value = true
                }
                .onFailure {
                    _deleteSuccess.value = false
                }
        }
    }
}
