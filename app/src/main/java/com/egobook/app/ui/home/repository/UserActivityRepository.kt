package com.egobook.app.ui.home.repository

import retrofit2.http.GET

@JvmInline
value class MissionWeekCount(val value: Int) {
    init {
        check(value >= 1) { "연속 수행 주차의 최소 값 1입니다" }
    }
}

enum class DayOfWeek(val order: Int) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    companion object {
        fun of(order: Int): DayOfWeek = checkNotNull(entries.find { it.order == order }) {
            "잘못된 요일 값입니다."
        }
    }
}

enum class MissionType {
    WRITING_EMOTION_DIARY,
    WRITING_LETTER,
    ANSWERING_DAILY_QUESTION
}

data class ActivityRecord(
    val dailyCompletedMissions: Set<MissionType>,
    val consecutiveCompletedWeekCount: MissionWeekCount,
    val weeklyCompletedDayOfWeek: Set<DayOfWeek>
)

interface UserActivityRepository {
    suspend fun loadActivityRecord(): ActivityRecord
}

interface NetworkActivityRecordService {
    @GET("/home/activities")
    suspend fun loadUserActivityRecord(): BaseResponse<ActivityRecordDto>
}

