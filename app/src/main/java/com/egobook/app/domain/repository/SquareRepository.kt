package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Friend

interface SquareRepository {
    suspend fun deleteFriend(deleteId: Int): Result<Int>
    suspend fun fetchFriendList(): Result<List<Friend>>
}
