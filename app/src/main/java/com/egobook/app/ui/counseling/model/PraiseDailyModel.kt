package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.counseling.PraiseDailyItem

data class PraiseDailyModel(
    val id: Int,
    val diaryDate: String,
    val isRead: Boolean
)

fun PraiseDailyItem.toPresentation(): PraiseDailyModel = PraiseDailyModel(
    id = id,
    diaryDate = diaryDate,
    isRead = isRead
)
