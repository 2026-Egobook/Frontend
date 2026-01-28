package com.egobook.app.domain.model.square.question

data class FriendTodayQuestionAnswer(
    val content: List<FriendTodayQuestionAnswerItem>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class FriendTodayQuestionAnswerItem(
    val answerId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val createdAt: String
)
