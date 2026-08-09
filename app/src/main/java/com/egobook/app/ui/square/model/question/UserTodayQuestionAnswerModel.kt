package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem

data class UserTodayQuestionAnswerModel(
    val content: List<UserTodayQuestionAnswerItemModel>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class UserTodayQuestionAnswerItemModel(
    val answerId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val createdAt: String,
    val level: Long,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null
)

fun UserTodayQuestionAnswerItem.toPresentation() = UserTodayQuestionAnswerItemModel(
    answerId = answerId,
    userId = userId,
    nickname = nickname,
    content = content,
    createdAt = createdAt,
    level = level,
    turtleImageUrl = turtleImageUrl,
    backgroundImageUrl = backgroundImageUrl
)

fun UserTodayQuestionAnswer.toPresentation() = UserTodayQuestionAnswerModel(
    content = content.map { it.toPresentation() },
    page = page,
    size = size,
    hasNext = hasNext
)
