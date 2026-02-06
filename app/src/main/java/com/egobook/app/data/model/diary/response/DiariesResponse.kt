package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiariesResponse (
    val dailyCount: Int,
    val diaries: DiarySliceResponse
)

@Serializable
data class DiarySliceResponse(
    val content: List<DiaryItemResponse>,
    val currentSlice: Long,
    val size: Long,
    val hasNext: Boolean
)

@Serializable
data class DiaryItemResponse(
    val diaryId: Long,
    val date: String,
    val writtenAt: String,
    val type: List<String>,
    val emotionLevel: Long?,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)