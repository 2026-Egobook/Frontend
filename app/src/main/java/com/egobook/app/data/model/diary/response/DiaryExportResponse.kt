package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryExportResponse(
    @SerialName("fileUrl")
    val fileUrl: String,
    @SerialName("expiresAt")
    val expiresAt: String,
    @SerialName("format")
    val format: String,
    @SerialName("range")
    val range: Range,
)

@Serializable
data class Range(
    val endDate: String,
    val startDate: String
)