package com.egobook.app.domain.model.square.question

data class MyTodayQuestionAnswer(
    val content: List<MyTodayQuestionAnswerItem>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class MyTodayQuestionAnswerItem(
    val questionId: Long,
    val questionDate: String,
    val questionContent: String,
    val answerId: Long,
    val answerContent: String,
    val visibility: AnswerVisibility,
    val answeredAt: String
)
