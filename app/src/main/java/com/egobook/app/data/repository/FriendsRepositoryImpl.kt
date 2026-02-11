package com.egobook.app.data.repository

import com.egobook.app.data.api.FriendsApiService
import com.egobook.app.data.model.square.friend.FriendRequestResponse
import com.egobook.app.data.model.square.friend.FriendResponse
import com.egobook.app.data.model.square.friend.SearchUserResponse
import com.egobook.app.data.model.square.friend.toDomain
import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.model.FriendRequest
import com.egobook.app.domain.model.SearchUser
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
            FriendResponse(id = 1, name = "소프트웨어마법사"),
            FriendResponse(id = 2, name = "야근하는다람쥐"),
            FriendResponse(id = 3, name = "커피중독자"),
            FriendResponse(id = 4, name = "안드로이드마스터"),
            FriendResponse(id = 5, name = "코딩하는고양이"),
            FriendResponse(id = 6, name = "말랑카우"),
            FriendResponse(id = 7, name = "개발하는진돗개"),
            FriendResponse(id = 8, name = "배고픈거북이"),
            FriendResponse(id = 9, name = "잠자는사자")
        )
        val emptyDummyData = listOf<FriendResponse>()
        Result.success(dummyData.map { it.toDomain()})
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchIncomingFriendRequestList(): Result<List<FriendRequest>> = try {
//        val response = apiService.fetchIncomingFriendsRequests()
//        if(response.status == 200) {
//            Result.success(response.data.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val dummyData = listOf(
            FriendRequestResponse(
                requestId = 9007199254740991L,
                userId = 1000000000000001L,
                nickname = "말랑카우",
                requestedAt = "2026-01-24T09:34:26.191Z"
            ),
            FriendRequestResponse(
                requestId = 9007199254740992L,
                userId = 1000000000000002L,
                nickname = "코딩하는고양이",
                requestedAt = "2026-01-24T10:15:00.000Z"
            ),
            FriendRequestResponse(
                requestId = 9007199254740993L,
                userId = 1000000000000003L,
                nickname = "안드로이드마스터",
                requestedAt = "2026-01-24T11:45:12.555Z"
            )
        )
        Result.success(dummyData.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchOutgoingFriendRequestList(): Result<List<FriendRequest>> = try {
//        val response = apiService.fetchOutgoingFriendsRequests()
//        if(response.status == 200) {
//            Result.success(response.data.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val dummyData = listOf(
            FriendRequestResponse(
                requestId = 8000000000000001L,
                userId = 2000000000000001L,
                nickname = "개발하는진돗개",
                requestedAt = "2026-01-24T10:00:00.000Z"
            ),
            FriendRequestResponse(
                requestId = 8000000000000002L,
                userId = 2000000000000002L,
                nickname = "소프트웨어마법사",
                requestedAt = "2026-01-24T11:30:00.000Z"
            ),
            FriendRequestResponse(
                requestId = 8000000000000003L,
                userId = 2000000000000003L,
                nickname = "커피중독자",
                requestedAt = "2026-01-24T13:15:00.000Z"
            ),
            FriendRequestResponse(
                requestId = 8000000000000004L,
                userId = 2000000000000004L,
                nickname = "야근하는다람쥐",
                requestedAt = "2026-01-24T15:45:00.000Z"
            )
        )
        Result.success(dummyData.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchUser(keyword: String): Result<List<SearchUser>> = try {
//        val response = apiService.searchUser(keyword = keyword)
//        if(response.status == 200) {
//            Result.success(response.data.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val dummySearchData = listOf(
            SearchUserResponse(
                userId = 1000000000000005L,
                nickname = "개발하는 거북이",
                level = 42,
                profileImageUrl = "https://images.unsplash.com/photo-1541344999736-83eca272f6fc?q=80"
            )
        )
        val dummySearchEmptyData = emptyList<SearchUserResponse>()
        Result.success(dummySearchData.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun requestFriendship(receiverId: Long): Result<Unit> = try {
//        val response = apiService.requestFriendship(request = FriendshipRequest(receiverId = receiverId))
//        if(response.status == 200) {
//            Result.success(Unit)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun rejectFriendRequest(requestId: Long): Result<Long> = try {
//        val response = apiService.rejectFriendRequest(requestId = requestId)
//        if(response.status == 200) {
//            Result.success(requestId)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(requestId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun acceptFriendRequest(requestId: Long): Result<Long> = try {
//        val response = apiService.acceptFriendRequest(requestId = requestId)
//        if(response.status == 200) {
//            Result.success(requestId)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(requestId)
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

    override suspend fun cancelFriendRequest(requestId: Long): Result<Long> = try {
//        val response = apiService.cancelFriendRequest(requestId = requestId)
//        if(response.status == 200) {
//            Result.success(requestId)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(requestId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}