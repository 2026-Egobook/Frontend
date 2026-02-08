package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.diary.mapper.DiaryEntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(DiariesState())  // 뷰모델 내부 갱신용
    val state = _state.asStateFlow()    // 외부(ui) 읽기 전용

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
        val diariesFlow = diaryUseCases
            .getDiaries(filter)
            .cachedIn(viewModelScope)
        
        _state.value = state.value.copy(diaries = diariesFlow)
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
    val diaries: Flow<PagingData<DiarySummary>> = emptyFlow(),
    val selectedTabType: Set<DiaryType>? = null,

    // 내부 로직용
    val selectedDate: LocalDate = LocalDate.now(),
    
    // UI 표시용
    val yearText: String = selectedDate.year.toString(),
    val monthText: String = selectedDate.monthValue.toString(),
    val dayText: String = selectedDate.dayOfMonth.toString()
)
