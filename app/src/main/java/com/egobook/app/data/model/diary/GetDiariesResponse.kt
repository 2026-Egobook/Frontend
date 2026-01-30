package com.egobook.app.data.model.diary


data class GetDiariesResponse (
    val dailyCount: Int,
    val diaries: DiarySliceResponse
)

data class DiarySliceResponse(
    val content: List<DiaryItemResponse>,
    val currentSlice: Long,
    val size: Long,
    val hasNext: Boolean
)

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