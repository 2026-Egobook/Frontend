package com.egobook.app.data.model.diary.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiariesRequest (
    @SerialName("date")
    val date: String,
    @SerialName("type")
    val type: String,
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int
)