package com.egobook.app.ui.square.model

import com.egobook.app.domain.model.Friend

data class FriendModel(
    val id: Int,
    val image: Int,
    val level: Int,
    val name: String
)

fun Friend.toPresentation(): FriendModel = FriendModel(
    id = id,
    image = image,
    level = level,
    name = name
)