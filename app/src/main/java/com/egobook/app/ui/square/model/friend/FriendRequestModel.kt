package com.egobook.app.ui.square.model.friend

import com.egobook.app.domain.model.FriendRequest

data class FriendRequestModel(
    val requestId: Long,
    val userId: Long,
    val nickname: String,
    val requestedAt: String
)

fun FriendRequest.toPresentation(): FriendRequestModel = FriendRequestModel(
    requestId = requestId,
    userId = userId,
    nickname = nickname,
    requestedAt = requestedAt
)