package com.egobook.app.data.repository

import com.egobook.app.R
import com.egobook.app.data.api.FriendsApiService
import com.egobook.app.data.model.square.FriendResponse
import com.egobook.app.data.model.square.toDomain
import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(private val apiService: FriendsApiService): FriendsRepository {
    override suspend fun fetchFriendList(): Result<List<Friend>> = try {
//        val response = apiService.fetchFriendList()
//        if(response.status == 200) {
//            Result.success(response.data.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val dummyData = listOf(
            FriendResponse(id = 1, name = "친구1"),
            FriendResponse(id = 2, name = "친구2"),
            FriendResponse(id = 3, name = "친구3")
        )
        Result.success(dummyData.map { it.toDomain()})
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFriend(deleteId: Long): Result<Long> = try {
//        val response = apiService.deleteFriend(friendId = deleteId)
//        if(response.status == 200) {
//            Result.success(deleteId)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(deleteId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}