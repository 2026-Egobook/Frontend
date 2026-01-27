package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
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

    // 💡 1. UiState 대신, nullable한 Diary 객체를 직접 상태로 관리합니다.
    private val _diary = MutableStateFlow<Diary?>(null)
    val diary = _diary.asStateFlow()

    init {
        val diaryId: Long = savedStateHandle.get<Long>("diaryId") ?: -1L
        if (diaryId != -1L) {
            getDiary(diaryId)
        }
    }

    private fun getDiary(id: Long) {
        viewModelScope.launch {
            diaryUseCases.getDiary(id)
                .onSuccess { fetchedDiary ->
                    // 💡 2. 성공 시, _diary StateFlow의 값을 직접 업데이트합니다.
                    _diary.value = fetchedDiary
                }
                .onFailure {
                    // 💡 3. 실패 시, 크래시를 방지하고 상태를 null로 유지합니다.
                    _diary.value = null
                }
        }
    }
}
