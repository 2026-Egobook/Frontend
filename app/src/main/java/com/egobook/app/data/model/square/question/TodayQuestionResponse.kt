package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.TodayQuestionAnswer
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.google.gson.annotations.SerializedName

data class TodayQuestionResponse(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("answered")
    val isUserAnswered: Boolean,
    @SerializedName("myAnswer")
    val myAnswer: TodayQuestionAnswerResponse? = null,
    @SerializedName("marketingEnabled")
    val marketingEnabled: Boolean = false
)

data class TodayQuestionAnswerResponse(
    @SerializedName("answerId")
    val answerId: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("visibility")
    val visibility: AnswerVisibility,
    @SerializedName("answeredAt")
    val answeredAt: String
)

fun TodayQuestionResponse.toDomain(): TodayQuestion = TodayQuestion(
    questionId = questionId,
    content = content,
    date = date,
    isUserAnswered = isUserAnswered,
    myAnswer = myAnswer?.toDomain(),
    marketingEnabled = marketingEnabled
)

fun TodayQuestionAnswerResponse.toDomain(): TodayQuestionAnswer = TodayQuestionAnswer(
    answerId = answerId,
    content = content,
    visibility = visibility,
    answeredAt = answeredAt
)


