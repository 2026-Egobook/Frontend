package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.calender.CalenderDate
import com.egobook.app.domain.usecase.CalenderUseCases
import com.egobook.app.ui.diary.mapper.CalenderEntityMapper
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CalenderViewModel @Inject constructor(
    private val calenderUseCases: CalenderUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(CalenderState())
    val state: StateFlow<CalenderState> = _state.asStateFlow()

    // 캘린더 데이터 로딩 상태 (로딩 뷰용)
    private val _calenderLoadState = MutableStateFlow<UiState<List<CalenderDate>>>(UiState.Idle)
    val calenderLoadState: StateFlow<UiState<List<CalenderDate>>> = _calenderLoadState.asStateFlow()

    // UI 상태 - 선택된 년월
    val selectedYearMonth: YearMonth
        get() = _state.value.selectedYearMonth

    /**
     * 초기 로드 - 외부에서 명시적으로 호출 필요
     */
    init {
        Log.d("ViewModel1", "=== ViewModel INIT === selectedYearMonth=${_state.value.selectedYearMonth}")
        // init에서 자동 로드하지 않음 - Fragment에서 초기 년월 설정 후 명시적 호출
    }

    /**
     * 초기 캘린더 데이터 로드 (Fragment에서 명시적 호출)
     */
    fun initializeCalendar() {
        loadCalender(_state.value.selectedYearMonth)
    }

    /**
     * 특정 월의 캘린더 데이터 로드
     */
    fun loadCalender(yearMonth: YearMonth) {
        viewModelScope.launch {
            Log.d("ViewModel1", "=== loadCalender START === yearMonth=$yearMonth")

            // YearMonth의 첫날을 LocalDate로 변환하여 API 호출
            val firstDayOfMonth = yearMonth.atDay(1)
            Log.d("ViewModel1", "firstDayOfMonth=$firstDayOfMonth, calling API...")

            calenderUseCases.getCalender(firstDayOfMonth)
                .onSuccess { calenderDates ->
                    Log.d("ViewModel1", "API Success, dates count: ${calenderDates.size}")
                    val emotionMap = CalenderEntityMapper.toDateEmotionMap(calenderDates)
                    Log.d("ViewModel1", "Map created with keys: ${emotionMap.keys}")
                    _state.update { state ->
                        state.copy(
                            selectedYearMonth = yearMonth,
                            calenderDates = calenderDates,
                            dateEmotionMap = emotionMap
                        )
                    }
                    Log.d("ViewModel1", "State updated, current map keys: ${_state.value.dateEmotionMap.keys}")
                }
                .onFailure { exception ->
                    Log.e("ViewModel1", "API Failure: ${exception.message}", exception)
                    // 실패해도 기존 데이터 유지, selectedYearMonth만 업데이트
                    _state.update { state ->
                        state.copy(
                            selectedYearMonth = yearMonth
                        )
                    }
                }
            Log.d("ViewModel1", "=== loadCalender END ===")
        }
    }

    /**
     * 이전 년도로 이동
     */
    fun onPreviousYear() {
        val newYearMonth = _state.value.selectedYearMonth.minusYears(1)
        loadCalender(newYearMonth)
    }

    /**
     * 다음 년도로 이동
     */
    fun onNextYear() {
        val newYearMonth = _state.value.selectedYearMonth.plusYears(1)
        loadCalender(newYearMonth)
    }

    /**
     * 특정 월 선택 (현재 선택된 년도에 해당 월 적용)
     */
    fun onMonthSelected(month: Int) {
        val newYearMonth = _state.value.selectedYearMonth.withMonth(month)
        loadCalender(newYearMonth)
    }

    /**
     * 특정 년월로 설정
     */
    fun setYearMonth(yearMonth: YearMonth) {
        loadCalender(yearMonth)
    }

    /**
     * 특정 날짜의 감정 레벨 조회
     */
    fun getEmotionLevel(date: LocalDate): Int? {
        return _state.value.dateEmotionMap[date]
    }
}

/**
 * 캘린더 상태
 */
data class CalenderState(
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val calenderDates: List<CalenderDate> = emptyList(),
    val dateEmotionMap: Map<LocalDate, Int?> = emptyMap()
)