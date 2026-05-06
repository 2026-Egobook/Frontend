package com.egobook.app.domain.model

data class Statistics(
    val totalStats: TotalStats,
    val moodPeak: MoodPeak,
    val stacked: StackedStats,
    val wordCloud: List<WordCloudItem>,
    val sixMonthAvgs: List<MonthlyAverage>,
    val generatedAt: String?
)

data class TotalStats(
    val maxCount: Int,
    val counts: List<EmotionCount>
)

data class EmotionCount(
    val emotionLevel: Int,
    val totalCount: Int
)

data class MoodPeak(
    val goodMood: MoodPeakTime?,
    val badMood: MoodPeakTime?
)

data class MoodPeakTime(
    val day: String,
    val hour: Int
)

data class StackedStats(
    val byWeekday: List<WeekdayStack>
)

data class WeekdayStack(
    val day: String,
    val levels: List<EmotionPercent>
)

data class EmotionPercent(
    val emotionLevel: Int,
    val percentOfMax: Int
)

data class WordCloudItem(
    val word: String,
    val weight: Int
)

data class MonthlyAverage(
    val year: Int,
    val month: Int,
    val avg: Float
)
