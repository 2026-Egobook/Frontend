package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalenderResponse(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int,
    @SerialName("data")
    val data: CalenderData?  // nullable로 변경
)

@Serializable
data class CalenderData(
    @SerialName("month")
    val month: String,
    @SerialName("days")
    val days: List<CalenderDay>? = null  // nullable + 기본값
)

@Serializable
data class CalenderDay(
    @SerialName("date")
    val date: String,
    @SerialName("emotionLevel")
    val emotionLevel: Int
)
