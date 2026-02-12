package com.egobook.app.data.model.diary.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryExportRequest (
    @SerialName("format")
    val format: String,

)