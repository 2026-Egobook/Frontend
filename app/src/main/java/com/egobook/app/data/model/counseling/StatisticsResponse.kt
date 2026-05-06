package com.egobook.app.data.model.counseling

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
import com.google.gson.annotations.SerializedName

data class StatisticsResponse(
    @SerializedName("totalStats")
    val totalStats: TotalStatsResponse?,
    @SerializedName("moodPeak")
    val moodPeak: MoodPeakResponse?,
    @SerializedName("stacked")
    val stacked: StackedStatsResponse?,
    @SerializedName("wordCloud")
    val wordCloud: List<WordCloudItemResponse>?,
    @SerializedName("sixMonthAvgs")
    val sixMonthAvgs: List<MonthlyAverageResponse>?,
    @SerializedName("generatedAt")
    val generatedAt: String?
)

data class TotalStatsResponse(
    @SerializedName("maxCount")
    val maxCount: Int?,
    @SerializedName("counts")
    val counts: List<EmotionCountResponse>?
)

data class EmotionCountResponse(
    @SerializedName("emotionLevel")
    val emotionLevel: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)

data class MoodPeakResponse(
    @SerializedName("goodMood")
    val goodMood: MoodPeakTimeResponse?,
    @SerializedName("badMood")
    val badMood: MoodPeakTimeResponse?
)

data class MoodPeakTimeResponse(
    @SerializedName("day")
    val day: String,
    @SerializedName("hour")
    val hour: Int
)

data class StackedStatsResponse(
    @SerializedName("byWeekday")
    val byWeekday: List<WeekdayStackResponse>?
)

data class WeekdayStackResponse(
    @SerializedName("day")
    val day: String,
    @SerializedName("levels")
    val levels: List<EmotionPercentResponse>?
)

data class EmotionPercentResponse(
    @SerializedName("emotionLevel")
    val emotionLevel: Int,
    @SerializedName("percentOfMax")
    val percentOfMax: Int
)

data class WordCloudItemResponse(
    @SerializedName("word")
    val word: String,
    @SerializedName("weight")
    val weight: Int
)

data class MonthlyAverageResponse(
    @SerializedName("year")
    val year: Int,
    @SerializedName("month")
    val month: Int,
    @SerializedName("avg")
    val avg: Float
)

fun StatisticsResponse.toDomain(): Statistics = Statistics(
    totalStats = totalStats?.toDomain() ?: TotalStats(maxCount = 0, counts = emptyList()),
    moodPeak = moodPeak?.toDomain() ?: MoodPeak(goodMood = null, badMood = null),
    stacked = stacked?.toDomain() ?: StackedStats(byWeekday = emptyList()),
    wordCloud = wordCloud.orEmpty().map { it.toDomain() },
    sixMonthAvgs = sixMonthAvgs.orEmpty().map { it.toDomain() },
    generatedAt = generatedAt
)

fun TotalStatsResponse.toDomain(): TotalStats = TotalStats(
    maxCount = maxCount ?: counts.orEmpty().maxOfOrNull { it.totalCount } ?: 0,
    counts = counts.orEmpty().map { it.toDomain() }
)

fun EmotionCountResponse.toDomain(): EmotionCount = EmotionCount(
    emotionLevel = emotionLevel,
    totalCount = totalCount
)

fun MoodPeakResponse.toDomain(): MoodPeak = MoodPeak(
    goodMood = goodMood?.toDomain(),
    badMood = badMood?.toDomain()
)

fun MoodPeakTimeResponse.toDomain(): MoodPeakTime = MoodPeakTime(
    day = day,
    hour = hour
)

fun StackedStatsResponse.toDomain(): StackedStats = StackedStats(
    byWeekday = byWeekday.orEmpty().map { it.toDomain() }
)

fun WeekdayStackResponse.toDomain(): WeekdayStack = WeekdayStack(
    day = day,
    levels = levels.orEmpty().map { it.toDomain() }
)

fun EmotionPercentResponse.toDomain(): EmotionPercent = EmotionPercent(
    emotionLevel = emotionLevel,
    percentOfMax = percentOfMax
)

fun WordCloudItemResponse.toDomain(): WordCloudItem = WordCloudItem(
    word = word,
    weight = weight
)

fun MonthlyAverageResponse.toDomain(): MonthlyAverage = MonthlyAverage(
    year = year,
    month = month,
    avg = avg
)
