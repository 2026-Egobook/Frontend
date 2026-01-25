package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(DiariesState())  // 뷰모델 내부 갱신용
    val state = _state.asStateFlow()    // 외부(ui) 읽기 전용

    private var getDiariesJob: Job? = null

    init {
        getDiaries(LocalDateTime.now(), null)
    }

    fun onEvent(event: DiariesEvent) {
        when(event) {
            is DiariesEvent.SwipeTab -> {
                getDiaries(state.value.selectedDate, event.types)
            }
            is DiariesEvent.ChangeDate -> {
                getDiaries(event.date, state.value.selectedTabType)
            }
        }
    }

    private fun getDiaries(selectedDate: LocalDateTime, types: Set<DiaryType>?) {
        getDiariesJob?.cancel()
        getDiariesJob = diaryUseCases.getDiaries(selectedDate, types)
            .onEach { diaries ->
                _state.value = state.value.copy(
                    diaries = diaries,
                    selectedTabType = types,
                    selectedDate = selectedDate
                )
            }
            .launchIn(viewModelScope)
    }
}

sealed class DiariesEvent {
    data class SwipeTab(val types: Set<DiaryType>?) : DiariesEvent()
    data class ChangeDate(val date: LocalDateTime) : DiariesEvent()
}

data class DiariesState(
    val diaries: List<Diary> = emptyList(),
    val selectedTabType: Set<DiaryType>? = null,
    val selectedDate: LocalDateTime = LocalDateTime.now()
)
