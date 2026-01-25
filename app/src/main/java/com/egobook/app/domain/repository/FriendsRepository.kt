package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.model.FriendRequest
import com.egobook.app.domain.model.SearchUser

interface FriendsRepository {
    suspend fun deleteFriend(deleteId: Long): Result<Long>
    suspend fun fetchFriendList(): Result<List<Friend>>
    suspend fun fetchIncomingFriendRequestList(): Result<List<FriendRequest>>
    suspend fun fetchOutgoingFriendRequestList(): Result<List<FriendRequest>>
    suspend fun searchUser(keyword: String): Result<List<SearchUser>>

    suspend fun requestFriendship(receiverId: Long): Result<Unit>
}
