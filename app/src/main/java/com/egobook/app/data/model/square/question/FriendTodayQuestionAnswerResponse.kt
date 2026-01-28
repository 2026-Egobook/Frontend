package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.square.question.FriendTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.FriendTodayQuestionAnswerItem
import com.google.gson.annotations.SerializedName

data class FriendTodayQuestionAnswerResponse(
    @SerializedName("content")
    val content: List<FriendTodayQuestionAnswerItemResponse>,
    @SerializedName("currentSlice")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class FriendTodayQuestionAnswerItemResponse(
    @SerializedName("answerId")
    val answerId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("createdAt")
    val createdAt: String
)

fun FriendTodayQuestionAnswerResponse.toDomain(): FriendTodayQuestionAnswer =
    FriendTodayQuestionAnswer(
        content = content.map { it.toDomain() },
        page = page,
        size = size,
        hasNext = hasNext
    )

fun FriendTodayQuestionAnswerItemResponse.toDomain(): FriendTodayQuestionAnswerItem =
    FriendTodayQuestionAnswerItem(
        answerId = answerId,
        userId = userId,
        nickname = nickname,
        content = content,
        createdAt = createdAt
    )
