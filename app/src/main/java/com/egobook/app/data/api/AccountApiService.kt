package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.account.AccountResponse
import com.egobook.app.data.model.account.DeleteAccountResponse
import com.egobook.app.data.model.account.LinkRequest
import com.egobook.app.data.model.account.LinkResponse
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AccountApiService {
    //유저 id 불러오기
    @GET("/home/settings")
    suspend fun getUserId(): ApiResponse<AccountResponse>

    @POST("/users/link/google")
    suspend fun linkToGoogle(
        @Body request: LinkRequest
    ): ApiResponse<LinkResponse>

    @DELETE("/users/withdraw")
    suspend fun deleteAccount(): ApiResponse<DeleteAccountResponse>

}