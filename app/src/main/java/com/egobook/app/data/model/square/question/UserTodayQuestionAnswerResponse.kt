package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import com.google.gson.annotations.SerializedName

data class UserTodayQuestionAnswerResponse(
    @SerializedName("content")
    val content: List<UserTodayQuestionAnswerItemResponse>,
    @SerializedName("currentSlice")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class UserTodayQuestionAnswerItemResponse(
    @SerializedName("answerId")
    val answerId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("level")
    val level: Long,
    @SerializedName("turtleImageUrl")
    val turtleImageUrl: String? = null,
    @SerializedName("backgroundImageUrl")
    val backgroundImageUrl: String? = null
)

fun UserTodayQuestionAnswerResponse.toDomain(): UserTodayQuestionAnswer =
    UserTodayQuestionAnswer(
        content = content.map { it.toDomain() },
        page = page,
        size = size,
        hasNext = hasNext
    )

fun UserTodayQuestionAnswerItemResponse.toDomain(): UserTodayQuestionAnswerItem =
    UserTodayQuestionAnswerItem(
        answerId = answerId,
        userId = userId,
        nickname = nickname,
        content = content,
        createdAt = createdAt,
        level = level,
        turtleImageUrl = turtleImageUrl,
        backgroundImageUrl = backgroundImageUrl
    )
