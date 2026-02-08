package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryCheckViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases,
    private val savedStateHandle: SavedStateHandle //다
) : ViewModel() {

    private val _diary = MutableStateFlow<Diary?>(null)
    val diary = _diary.asStateFlow()

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess = _deleteSuccess.asStateFlow()

    private val diaryId: Long = savedStateHandle.get<Long>("diaryId") ?: -1L

    init {
        if (diaryId != -1L) {
            getDiary(diaryId)
        }
    }

    private fun getDiary(id: Long) {
        viewModelScope.launch {
            diaryUseCases.getDiary(id)
                .onSuccess { fetchedDiary ->
                    // _diary StateFlow의 값을 직접 업데이트.
                    _diary.value = fetchedDiary
                }
                .onFailure {
                    // 실패 시, 크래시를 방지하고 상태를 null로 유지.
                    _diary.value = null
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
