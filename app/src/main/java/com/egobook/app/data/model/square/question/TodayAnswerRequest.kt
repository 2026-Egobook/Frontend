package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.google.gson.annotations.SerializedName

data class TodayAnswerRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("visibility")
    val visibilityType: AnswerVisibility
)
