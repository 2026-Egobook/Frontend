package com.egobook.app.ui.home.repository

data class ActivityRecordDto(
    val isDailyMissionSuccess: Boolean,
    val hasWrittenDiary: Boolean,
    val hasWrittenLetter: Boolean,
    val hasAnsweredQuestion: Boolean,
    val consecutiveWeeks: Int,
    val weeklyMissionStatus: List<Boolean>
) {
    fun toDomain(): ActivityRecord {
        return ActivityRecord(
            dailyCompletedMissions = setOfNotNull(
                if (hasAnsweredQuestion) MissionType.ANSWERING_DAILY_QUESTION else null,
                if (hasWrittenDiary) MissionType.WRITING_EMOTION_DIARY else null,
                if (hasWrittenLetter) MissionType.WRITING_LETTER else null
            ),
            consecutiveCompletedWeekCount = MissionWeekCount(consecutiveWeeks),
            weeklyCompletedDayOfWeek = weeklyMissionStatus.withIndex().filter { it.value }
                .map { DayOfWeek.of(it.index) }.toSet()
        )
    }
}
