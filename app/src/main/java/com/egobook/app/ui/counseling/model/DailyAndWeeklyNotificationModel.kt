package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification

data class DailyAndWeeklyNotificationModel(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyAnalysisEnabled: Boolean
)

fun DailyAndWeeklyNotification.toPresentation() = DailyAndWeeklyNotificationModel(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyAnalysisEnabled = isWeeklyAnalysisEnabled
)
