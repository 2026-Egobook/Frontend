package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.TodayQuestionAnswer
import com.egobook.app.domain.model.square.question.AnswerVisibility

data class TodayQuestionModel(
    val questionId: Long,
    val content: String,
    val date: String,
    val isUserAnswered: Boolean,
    val myAnswer: TodayQuestionAnswerModel? = null,
    val marketingEnabled: Boolean = false
)

data class TodayQuestionAnswerModel(
    val answerId: Long,
    val content: String,
    val visibility: AnswerVisibility,
    val answeredAt: String
)

fun TodayQuestion.toPresentation(): TodayQuestionModel = TodayQuestionModel(
    questionId = questionId,
    content = content,
    date = date,
    isUserAnswered = isUserAnswered,
    myAnswer = myAnswer?.toPresentation(),
    marketingEnabled = marketingEnabled
)

fun TodayQuestionAnswer.toPresentation(): TodayQuestionAnswerModel = TodayQuestionAnswerModel(
    answerId = answerId,
    content = content,
    visibility = visibility,
    answeredAt = answeredAt
)

