package com.egobook.app.data.model.square.question

import com.google.gson.annotations.SerializedName

data class TodayAnswerRequest(
    @SerializedName(/* value = */ "content")
    val content: String,
    @SerializedName("visibility")
    val visibilityType: String
)
