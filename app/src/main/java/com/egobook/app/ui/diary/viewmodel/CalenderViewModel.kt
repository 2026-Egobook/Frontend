package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.YearMonth

class CalenderViewModel : ViewModel() {

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth

    /**
     * 이전 년도로 이동
     */
    fun onPreviousYear() {
        _selectedYearMonth.value = _selectedYearMonth.value.minusYears(1)
    }

    /**
     * 다음 년도로 이동
     */
    fun onNextYear() {
        _selectedYearMonth.value = _selectedYearMonth.value.plusYears(1)
    }

    /**
     * 특정 월 선택 (현재 선택된 년도에 해당 월 적용)
     */
    fun onMonthSelected(month: Int) {
        _selectedYearMonth.value = _selectedYearMonth.value.withMonth(month)
    }

    /**
     * 특정 년월로 설정
     */
    fun setYearMonth(yearMonth: YearMonth) {
        _selectedYearMonth.value = yearMonth
    }
}