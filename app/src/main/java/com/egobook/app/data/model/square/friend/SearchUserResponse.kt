package com.egobook.app.data.model.square.friend

import com.egobook.app.domain.model.SearchUser
import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("level")
    val level: Long,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)

fun SearchUserResponse.toDomain(): SearchUser = SearchUser(
    userId = userId,
    nickname = nickname,
    level = level,
    profileImageUrl = profileImageUrl
)
