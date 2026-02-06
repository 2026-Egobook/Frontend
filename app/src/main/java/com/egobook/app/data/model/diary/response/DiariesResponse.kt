package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiariesResponse (
    @SerialName("dailyCount")
    val dailyCount: Int,
    @SerialName("diaries")
    val diaries: DiarySlice
)

@Serializable
data class DiarySlice(
    @SerialName("content")
    val content: List<DiaryItem>,
    @SerialName("page")
    val page: Long,
    @SerialName("size")
    val size: Long,
    @SerialName("hasNext")
    val hasNext: Boolean
)

@Serializable
data class DiaryItem(
    @SerialName("diaryId")
    val diaryId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("writtenAt")
    val writtenAt: String,
    @SerialName("type")
    val type: List<String>,
    @SerialName("emotionLevel")
    val emotionLevel: Int?,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String
)