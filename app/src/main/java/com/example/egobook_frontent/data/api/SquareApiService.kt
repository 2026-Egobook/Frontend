package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.square.FriendResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface SquareApiService {
    @GET("api/square/friend")
    suspend fun fetchFriendList(): Response<List<FriendResponse>>

    @DELETE("api/square/friend/{deleteId}")
    suspend fun deleteFriend(@Path("deleteId") deleteId: Int): Response<Unit>
}

