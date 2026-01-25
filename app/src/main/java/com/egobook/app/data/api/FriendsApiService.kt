package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.FriendRequestResponse
import com.egobook.app.data.model.square.FriendResponse
import com.egobook.app.data.model.square.FriendshipRequest
import com.egobook.app.data.model.square.SearchUserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendsApiService {
    @GET("/friends")
    suspend fun fetchFriendList(): ApiResponse<List<FriendResponse>>

    @DELETE("/friends/{friendId}")
    suspend fun deleteFriend(@Path("friendId") friendId: Int): ApiResponse<Unit>

    @GET("/friends/requests/incoming")
    suspend fun fetchIncomingFriendsRequests(): ApiResponse<List<FriendRequestResponse>>

    @GET("/friends/requests/outgoing")
    suspend fun fetchOutgoingFriendsRequests(): ApiResponse<List<FriendRequestResponse>>

    @GET("/friends/search")
    suspend fun searchUser(@Query("keyword") keyword: String): ApiResponse<List<SearchUserResponse>>

    @POST("/friends/requests")
    suspend fun requestFriendship(@Body request: FriendshipRequest): ApiResponse<Unit>

    @POST("/friends/requests/{requestId}/reject")
    suspend fun rejectFriendRequest(@Path("requestId") requestId: Long): ApiResponse<Unit>

    @POST("friends/requests/{requestId}/accept")
    suspend fun acceptFriendRequest(@Path("requestId") requestId: Long): ApiResponse<Unit>
}

