package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.google.gson.annotations.SerializedName

data class DailyAndWeeklyNotificationResponse(
    @SerializedName("dailyPraiseEnabled")
    val isDailyPraiseEnabled: Boolean,
    @SerializedName("weeklyAnalysisEnabled")
    val isWeeklyAnalysisEnabled: Boolean
)

fun DailyAndWeeklyNotificationResponse.toDomain() = DailyAndWeeklyNotification(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyAnalysisEnabled = isWeeklyAnalysisEnabled
)


