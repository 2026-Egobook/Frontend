package com.egobook.app.data.model.square.friend

import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.model.FriendList
import com.google.gson.annotations.SerializedName

data class FriendListResponse(
    val count: Int,
    val friends: List<FriendResponse>,
)

data class FriendResponse(
    @SerializedName("friendId")
    val id: Long,
    @SerializedName("nickname")
    val name: String,
    @SerializedName("level")
    val level: Long
)

fun FriendListResponse.toDomain(): FriendList = FriendList(
    count = count,
    friends = friends.map { it.toDomain() }
)

fun FriendResponse.toDomain(): Friend = Friend(
    id = id,
    name = name,
    level = level
)

