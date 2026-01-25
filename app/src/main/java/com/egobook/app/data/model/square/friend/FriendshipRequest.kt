package com.egobook.app.data.model.square.friend

import com.google.gson.annotations.SerializedName

data class FriendshipRequest(
    @SerializedName("receiverId")
    val receiverId: Long
)
