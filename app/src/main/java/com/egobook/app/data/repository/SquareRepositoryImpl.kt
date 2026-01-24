package com.egobook.app.data.repository

import com.egobook.app.R
import com.egobook.app.data.api.SquareApiService
import com.egobook.app.data.model.square.FriendResponse
import com.egobook.app.data.model.square.toDomain
import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.repository.SquareRepository
import javax.inject.Inject

class SquareRepositoryImpl @Inject constructor(private val apiService: SquareApiService): SquareRepository {
    override suspend fun fetchFriendList(): Result<List<Friend>> = try {
//        val response = apiService.fetchFriendList()
//        if(response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        val dummyData = listOf(
            FriendResponse(id = 1, image = R.drawable.default_turtle, level = 1, name = "친구1"),
            FriendResponse(id = 2, image = R.drawable.default_turtle, level = 2, name = "친구2"),
            FriendResponse(id = 3, image = R.drawable.default_turtle, level = 3, name = "친구3")
        )
        Result.success(dummyData.map { it.toDomain()})
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFriend(deleteId: Int): Result<Int> = try {
//        val response = apiService.deleteFriend(deleteId = deleteId)
//        if(response.isSuccessful) {
//            Result.success(deleteId)
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        Result.success(deleteId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}