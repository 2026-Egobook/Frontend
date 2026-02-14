package com.egobook.app.ui.square.model.friend

import com.egobook.app.domain.model.SearchUser

data class SearchUserModel(
    val userId: Long,
    val nickname: String,
    val level: Long,
    val profileImageUrl: String? = null
)

fun SearchUser.toPresentation(): SearchUserModel = SearchUserModel(
    userId = userId,
    nickname = nickname,
    level = level,
    profileImageUrl = profileImageUrl
)

