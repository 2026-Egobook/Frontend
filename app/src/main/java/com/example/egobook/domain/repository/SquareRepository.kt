package com.example.egobook.domain.repository

import com.example.egobook.domain.model.Friend

interface SquareRepository {
    suspend fun deleteFriend(deleteId: Int): Result<Int>
    suspend fun fetchFriendList(): Result<List<Friend>>
}
