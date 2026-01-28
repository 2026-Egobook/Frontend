package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.square.question.FriendTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.FriendTodayQuestionAnswerItem

data class FriendTodayQuestionAnswerModel(
    val content: List<FriendTodayQuestionAnswerItemModel>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class FriendTodayQuestionAnswerItemModel(
    val answerId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val createdAt: String
)

fun FriendTodayQuestionAnswerItem.toPresentation() = FriendTodayQuestionAnswerItemModel(
    answerId = answerId,
    userId = userId,
    nickname = nickname,
    content = content,
    createdAt = createdAt
)

fun FriendTodayQuestionAnswer.toPresentation() = FriendTodayQuestionAnswerModel(
    content = content.map { it.toPresentation() },
    page = page,
    size = size,
    hasNext = hasNext
)
