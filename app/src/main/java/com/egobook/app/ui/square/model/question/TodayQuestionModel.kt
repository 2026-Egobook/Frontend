package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.TodayQuestion

data class TodayQuestionModel(
    val questionId: Long,
    val content: String,
    val date: String,
    val isUserAnswered: Boolean
)

fun TodayQuestion.toPresentation(): TodayQuestionModel = TodayQuestionModel(
    questionId = questionId,
    content = content,
    date = date,
    isUserAnswered = isUserAnswered
)
