package com.egobook.app.ui.square.model.friend

import com.egobook.app.domain.model.FriendRequest

data class FriendRequestModel(
    val requestId: Long,
    val userId: Long,
    val nickname: String,
    val level: Long,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
)

fun FriendRequest.toPresentation(): FriendRequestModel = FriendRequestModel(
    requestId = requestId,
    userId = userId,
    nickname = nickname,
    level = level,
    turtleImageUrl = turtleImageUrl,
    backgroundImageUrl = backgroundImageUrl
)
