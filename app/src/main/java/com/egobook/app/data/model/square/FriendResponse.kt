package com.egobook.app.data.model.square

import com.egobook.app.domain.model.Friend
import com.google.gson.annotations.SerializedName

data class FriendResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: Int,
    @SerializedName("level")
    val level: Int
)

fun FriendResponse.toDomain(): Friend = Friend(
    id = id,
    name = name,
    image = image,
    level = level
)
