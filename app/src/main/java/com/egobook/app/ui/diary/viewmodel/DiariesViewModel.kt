package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases
) : ViewModel() { // 💡 ViewModel()을 상속해야 합니다.
    private val _state = MutableStateFlow(DiariesState())
    val state = _state.asStateFlow()

    private var getDiariesJob: Job? = null

    init {
        getDiaries(null)
    }

    fun onEvent(event: DiariesEvent) {
        when(event) {
            is DiariesEvent.SwipeTab -> {
                getDiaries(event.types)
            }
        }
    }

    private fun getDiaries(types: Set<DiaryType>?) {
        getDiariesJob?.cancel()
        getDiariesJob = diaryUseCases.getDiaries(types)
            .onEach { diaries ->
                _state.value = _state.value.copy(
                    diaries = diaries,
                    selectedTabType = types
                )
            }
            .launchIn(viewModelScope)
    }
}

sealed class DiariesEvent {
    data class SwipeTab(val types: Set<DiaryType>?) : DiariesEvent()
}

data class DiariesState(
    val diaries: List<Diary> = emptyList(),
    val selectedTabType: Set<DiaryType>? = null,
    val isLoading: Boolean = false
)
