package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.account.AccountResponse
import retrofit2.http.GET
import retrofit2.Response

interface AccountApiService {
    //유저 id 불러오기
    @GET("/home/settings")
    suspend fun getUserId(): ApiResponse<AccountResponse>
}