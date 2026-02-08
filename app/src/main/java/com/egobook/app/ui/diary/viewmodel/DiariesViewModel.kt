package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.diary.mapper.DiaryEntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(DiariesState())  // 뷰모델 내부 갱신용
    val state = _state.asStateFlow()    // 외부(ui) 읽기 전용

    private var getDiariesJob: Job? = null

    init {
        loadDiaries(LocalDate.now(), null)
    }

    fun onEvent(event: DiariesEvent) {
        when(event) {
            is DiariesEvent.SwipeTab -> {
                val domainTypes = event.displayTypes?.let {
                    DiaryEntityMapper.uiDisplayTypesToDomain(it)
                }

                _state.value = state.value.copy(selectedTabType = domainTypes)
                loadDiaries(state.value.selectedDate, domainTypes)
            }
            is DiariesEvent.ChangeDate -> {
                // 도메인 엔티티 형식으로 날짜 변환
                val date = DiaryEntityMapper.uiYearMonthDateToDomain(
                    event.year,
                    event.month,
                    event.day,
                )
                _state.value = state.value
                    .withDate(date)
                    .copy(selectedTabType = null)
                loadDiaries(date, null) // 날짜 변경 시 "전체" 탭으로 리셋
            }
        }
    }

    //상태를 보지 말고 뷰모델 내부 state 기반으로만 동작
    private fun loadDiaries(selectedDate: LocalDate, types: Set<DiaryType>?) {
        val filter = DiaryFilter(selectedDate, types)
        val currentState = state.value

        getDiariesJob?.cancel()
        getDiariesJob = diaryUseCases
            .getDiaries(filter)
            .onEach { diaries ->
                _state.value = currentState.copy(diaries = diaries)
            }
            .launchIn(viewModelScope)
    }

    // 날짜가 바뀌면 UI 표시값까지 자동 변경하는 확장함수
    private fun DiariesState.withDate(date: LocalDate): DiariesState {
        return copy(
            selectedDate = date,
            yearText = date.year.toString(),
            monthText = date.monthValue.toString(),
            dayText = date.dayOfMonth.toString()
        )
    }
}

sealed class DiariesEvent {
    data class SwipeTab(val displayTypes: Set<String>?) : DiariesEvent()
    data class ChangeDate(val year: Int, val month: Int, val day: Int) : DiariesEvent()

}

data class DiariesState(
    val diaries: List<DiarySummary> = emptyList(),
    val selectedTabType: Set<DiaryType>? = null,

    // UI 표시용
    val yearText: String = "",
    val monthText: String = "",
    val dayText: String = "",

    // 내부 로직용
    val selectedDate: LocalDate = LocalDate.now()
)
