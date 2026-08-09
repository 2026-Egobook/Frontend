package com.egobook.app.ui.square.model.friend

import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.model.FriendList

data class FriendListModel(
    val count: Int,
    val friends: List<FriendModel>,
)
data class FriendModel(
    val id: Long,
    val name: String,
    val level: Long,
    val turtleImageUrl: String? = null,
    val backgroundImageUrl: String? = null
)

fun FriendList.toPresentation(): FriendListModel = FriendListModel(
    count = count,
    friends = friends.map { it.toPresentation() }
)

fun Friend.toPresentation(): FriendModel = FriendModel(
    id = id,
    name = name,
    level = level,
    turtleImageUrl = turtleImageUrl,
    backgroundImageUrl = backgroundImageUrl
)
