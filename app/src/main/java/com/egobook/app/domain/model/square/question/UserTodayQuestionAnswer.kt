package com.egobook.app.domain.model.square.question

data class UserTodayQuestionAnswer(
    val content: List<UserTodayQuestionAnswerItem>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class UserTodayQuestionAnswerItem(
    val answerId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val createdAt: String,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null
)
