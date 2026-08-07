package com.egobook.app.domain.model

import com.egobook.app.domain.model.square.question.AnswerVisibility

data class TodayQuestion(
    val questionId: Long,
    val content: String,
    val date: String,
    val isUserAnswered: Boolean,
    val myAnswer: TodayQuestionAnswer? = null,
    val marketingEnabled: Boolean = false
)

data class TodayQuestionAnswer(
    val answerId: Long,
    val content: String,
    val visibility: AnswerVisibility,
    val answeredAt: String
)
