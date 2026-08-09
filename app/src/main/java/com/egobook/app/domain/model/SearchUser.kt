package com.egobook.app.domain.model

data class SearchUser(
    val userId: Long,
    val nickname: String,
    val level: Long,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null
)
