package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.DailyData
import com.egobook.app.domain.model.EmotionType
import com.egobook.app.domain.model.MonthData
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.TimeData

data class StatisticsResponse(
    val emotions: Map<EmotionType, MonthDataResponse>
)

data class MonthDataResponse(val months: List<DailyDataResponse>) {
    val totalCnt: Int get() = months.sumOf { it.totalCnt } // 해당 감정의 1년 전체 합계
}

data class DailyDataResponse(val days: List<TimeDataResponse>) {
    val totalCnt: Int get() = days.sumOf { it.totalCnt } // 해당 달의 전체 합계
}

data class TimeDataResponse(val hours: List<Int>) {
    val totalCnt: Int get() = hours.sumOf { it }
}

fun StatisticsResponse.toDomain(): Statistics = Statistics(
    emotions = emotions.mapValues { it.value.toDomain() }
)

fun MonthDataResponse.toDomain(): MonthData = MonthData(
    months = months.map { it.toDomain() }
)

fun DailyDataResponse.toDomain(): DailyData = DailyData(
    days = days.map { it.toDomain() }
)

fun TimeDataResponse.toDomain(): TimeData = TimeData(
    hours = hours
)