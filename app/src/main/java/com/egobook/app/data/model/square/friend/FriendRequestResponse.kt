package com.egobook.app.data.model.square.friend

import com.egobook.app.domain.model.FriendRequest
import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("requestId")
    val requestId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("level")
    val level: Long,
    @SerializedName("turtleImageUrl")
    val turtleImageUrl: String? = null,
    @SerializedName("backgroundImageUrl")
    val backgroundImageUrl: String? = null,
    @SerializedName("requestedAt")
    val requestedAt: String
)

fun FriendRequestResponse.toDomain(): FriendRequest = FriendRequest(
    requestId = requestId,
    userId = userId,
    nickname = nickname,
    level = level,
    turtleImageUrl = turtleImageUrl,
    backgroundImageUrl = backgroundImageUrl,
    requestedAt = requestedAt
)
