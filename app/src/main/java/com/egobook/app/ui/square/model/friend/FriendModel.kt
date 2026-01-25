package com.egobook.app.ui.square.model.friend

import com.egobook.app.domain.model.Friend

data class FriendModel(
    val id: Long,
    val name: String
)

fun Friend.toPresentation(): FriendModel = FriendModel(
    id = id,
    name = name
)