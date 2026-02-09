package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.counseling.PraiseDailyItem
import com.google.gson.annotations.SerializedName

data class PraiseDailyResponse(
    @SerializedName("content")
    val content: List<PraiseDailyItemResponse>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class PraiseDailyItemResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("diaryDate")
    val diaryDate: String,
    @SerializedName("isRead")
    val isRead: Boolean
)

fun PraiseDailyItemResponse.toDomain(): PraiseDailyItem = PraiseDailyItem(
    id = id,
    diaryDate = diaryDate,
    isRead = isRead
)
