package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.EmotionCount
import com.egobook.app.domain.model.EmotionPercent
import com.egobook.app.domain.model.MonthlyAverage
import com.egobook.app.domain.model.MoodPeak
import com.egobook.app.domain.model.MoodPeakTime
import com.egobook.app.domain.model.StackedStats
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.TotalStats
import com.egobook.app.domain.model.WeekdayStack
import com.egobook.app.domain.model.WordCloudItem

data class StatisticsModel(
    val totalStats: TotalStats,
    val moodPeak: MoodPeak,
    val stacked: StackedStats,
    val wordCloud: List<WordCloudItem>,
    val sixMonthAvgs: List<MonthlyAverage>,
    val generatedAt: String?
)

fun Statistics.toPresentation(): StatisticsModel = StatisticsModel(
    totalStats = totalStats,
    moodPeak = moodPeak,
    stacked = stacked,
    wordCloud = wordCloud,
    sixMonthAvgs = sixMonthAvgs,
    generatedAt = generatedAt
)
