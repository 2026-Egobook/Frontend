package com.egobook.app.domain.model

data class FriendRequest(
    val requestId: Long,
    val userId: Long,
    val nickname: String,
    val level: Long,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
    val requestedAt: String
)
