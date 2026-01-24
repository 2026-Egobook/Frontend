package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.FriendRequestResponse
import com.egobook.app.data.model.square.FriendResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface FriendsApiService {
    @GET("/friends")
    suspend fun fetchFriendList(): ApiResponse<List<FriendResponse>>

    @DELETE("/friends/{friendId}")
    suspend fun deleteFriend(@Path("friendId") friendId: Int): ApiResponse<Unit>

    @GET("/friends/requests/incoming")
    suspend fun fetchIncomingFriendsRequests(): ApiResponse<List<FriendRequestResponse>>
}

