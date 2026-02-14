package com.egobook.app.data.repository

import com.egobook.app.data.api.FriendsApiService
import com.egobook.app.data.model.square.friend.FriendshipRequest
import com.egobook.app.data.model.square.friend.toDomain
import com.egobook.app.domain.model.FriendList
import com.egobook.app.domain.model.FriendRequest
import com.egobook.app.domain.model.SearchUser
import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(private val apiService: FriendsApiService): FriendsRepository {
    override suspend fun fetchFriendList(): Result<FriendList> = try {
        val response = apiService.fetchFriendList()
        if(response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchIncomingFriendRequestList(): Result<List<FriendRequest>> = try {
        val response = apiService.fetchIncomingFriendsRequests()
        if(response.status == 200) {
            Result.success(response.data.map { it.toDomain() })
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchOutgoingFriendRequestList(): Result<List<FriendRequest>> = try {
        val response = apiService.fetchOutgoingFriendsRequests()
        if(response.status == 200) {
            Result.success(response.data.map { it.toDomain() })
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchUser(keyword: String): Result<List<SearchUser>?> = try {
        val response = apiService.searchUser(keyword = keyword)
        if(response.status == 200) {
            Result.success(response.data?.map { it.toDomain() })
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun requestFriendship(receiverId: Long): Result<Unit> = try {
        val response = apiService.requestFriendship(request = FriendshipRequest(receiverId = receiverId))
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun rejectFriendRequest(requestId: Long): Result<Long> = try {
        val response = apiService.rejectFriendRequest(requestId = requestId)
        if(response.status == 200) {
            Result.success(requestId)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun acceptFriendRequest(requestId: Long): Result<Long> = try {
        val response = apiService.acceptFriendRequest(requestId = requestId)
        if(response.status == 200) {
            Result.success(requestId)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFriend(deleteId: Long): Result<Long> = try {
        val response = apiService.deleteFriend(friendId = deleteId)
        if(response.status == 200) {
            Result.success(deleteId)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun cancelFriendRequest(requestId: Long): Result<Long> = try {
        val response = apiService.cancelFriendRequest(requestId = requestId)
        if(response.status == 200) {
            Result.success(requestId)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}