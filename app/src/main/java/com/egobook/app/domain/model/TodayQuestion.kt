package com.egobook.app.domain.model

data class TodayQuestion(
    val questionId: Long,
    val content: String,
    val date: String,
    val isUserAnswered: Boolean
)
