package com.egobook.app.domain.model

data class FriendList(
    val count: Int,
    val friends: List<Friend>,
)

data class Friend(
    val id: Long,
    val name: String,
    val level: Long
)