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
    val content: List<DiaryEntryResponse>,

    @SerialName("page")
    val page: Int,

    @SerialName("size")
    val size: Int,

    @SerialName("hasNext")
    val hasNext: Boolean
)