package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryExportResponse(
    @SerialName("fileUrl")
    val fileUrl: String,
)
