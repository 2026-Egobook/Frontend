package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.counseling.DailyPraise

data class PraiseDailyModel(
    val id: Int,
    val diaryDate: String,
    val isRead: Boolean
)

fun DailyPraise.toPresentation(): PraiseDailyModel = PraiseDailyModel(
    id = id,
    diaryDate = diaryDate,
    isRead = isRead
)
