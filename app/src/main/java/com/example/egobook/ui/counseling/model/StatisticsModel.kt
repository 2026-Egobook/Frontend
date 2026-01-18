package com.example.egobook.ui.counseling.model

import com.example.egobook.domain.model.DailyData
import com.example.egobook.domain.model.EmotionType
import com.example.egobook.domain.model.MonthData
import com.example.egobook.domain.model.Statistics
import com.example.egobook.domain.model.TimeData

data class StatisticsModel(
    val emotions: Map<EmotionType, MonthDataModel>
)

data class MonthDataModel(val months: List<DailyDataModel>) {
    val totalCnt: Int get() = months.sumOf { it.totalCnt } // 1년 데이터 합계
}

data class DailyDataModel(val days: List<TimeDataModel>) {
    val totalCnt: Int get() = days.sumOf { it.totalCnt } // 해당 달의 전체 합계
}

data class TimeDataModel(val hours: List<Int>) {
    val totalCnt: Int get() = hours.sum() // 해당 요일의 전체 합계
}

fun Statistics.toPresentation(): StatisticsModel = StatisticsModel(
    emotions = emotions.mapValues { it.value.toPresentation() }
)

fun MonthData.toPresentation(): MonthDataModel = MonthDataModel(
    months = months.map { it.toPresentation() }
)

fun DailyData.toPresentation(): DailyDataModel = DailyDataModel(
    days = days.map { it.toPresentation() }
)

fun TimeData.toPresentation(): TimeDataModel = TimeDataModel(
    hours = hours
)
