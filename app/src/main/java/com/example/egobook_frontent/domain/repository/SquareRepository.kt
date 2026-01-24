package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.Friend

interface SquareRepository {
    suspend fun deleteFriend(deleteId: Int): Result<Int>
    suspend fun fetchFriendList(): Result<List<Friend>>
}
