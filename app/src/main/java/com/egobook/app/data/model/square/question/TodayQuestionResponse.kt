package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.TodayQuestion
import com.google.gson.annotations.SerializedName

data class TodayQuestionResponse(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("answered")
    val isUserAnswered: Boolean
)

fun TodayQuestionResponse.toDomain(): TodayQuestion = TodayQuestion(
    questionId = questionId,
    content = content,
    date = date,
    isUserAnswered = isUserAnswered
)

