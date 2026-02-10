package com.egobook.app.data.model.diary.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryUpdateRequest (
    @SerialName("type")
    val type: List<String>,
    @SerialName("emotionLevel")
    val emotionLevel: Int?,
    @SerialName("content")
    val content: String
)