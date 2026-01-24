package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Friend

interface FriendsRepository {
    suspend fun deleteFriend(deleteId: Long): Result<Long>
    suspend fun fetchFriendList(): Result<List<Friend>>
}
