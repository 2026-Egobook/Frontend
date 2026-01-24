package com.egobook.app.data.model.square

import com.egobook.app.domain.model.Friend
import com.google.gson.annotations.SerializedName

data class FriendResponse(
    @SerializedName("friendId")
    val id: Long,
    @SerializedName("nickname")
    val name: String,
)

fun FriendResponse.toDomain(): Friend = Friend(
    id = id,
    name = name
)
