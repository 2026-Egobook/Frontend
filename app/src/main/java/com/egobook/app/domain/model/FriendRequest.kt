package com.egobook.app.domain.model

data class FriendRequest(
    val requestId: Long,
    val userId: Long,
    val nickname: String,
    val requestedAt: String
)