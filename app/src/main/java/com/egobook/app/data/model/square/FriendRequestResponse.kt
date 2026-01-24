package com.egobook.app.data.model.square

import com.egobook.app.domain.model.FriendRequest
import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("requestId")
    val requestId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("requestedAt")
    val requestedAt: String
)

fun FriendRequestResponse.toDomain(): FriendRequest = FriendRequest(
    requestId = requestId,
    userId = userId,
    nickname = nickname,
    requestedAt = requestedAt
)
