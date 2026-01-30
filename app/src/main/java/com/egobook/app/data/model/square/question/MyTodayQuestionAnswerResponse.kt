package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.google.gson.annotations.SerializedName

data class MyTodayQuestionAnswerResponse(
    @SerializedName("content")
    val content: List<MyTodayQuestionAnswerItemResponse>,
    @SerializedName("currentSlice")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class MyTodayQuestionAnswerItemResponse(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("questionDate")
    val questionDate: String,
    @SerializedName("questionContent")
    val questionContent: String,
    @SerializedName("answerId")
    val answerId: Long,
    @SerializedName("answerContent")
    val answerContent: String,
    @SerializedName("visibility")
    val visibility: AnswerVisibility,
    @SerializedName("answeredAt")
    val answeredAt: String
)

fun MyTodayQuestionAnswerResponse.toDomain(): MyTodayQuestionAnswer = MyTodayQuestionAnswer(
    content = content.map { it.toDomain() },
    page = page,
    size = size,
    hasNext = hasNext
)

fun MyTodayQuestionAnswerItemResponse.toDomain(): MyTodayQuestionAnswerItem = MyTodayQuestionAnswerItem(
    questionId = questionId,
    questionDate = questionDate,
    questionContent = questionContent,
    answerId = answerId,
    answerContent = answerContent,
    visibility = visibility,
    answeredAt = answeredAt
)