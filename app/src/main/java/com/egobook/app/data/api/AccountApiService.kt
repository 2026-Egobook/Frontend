package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.account.AccountResponse
import com.egobook.app.data.model.account.DeleteAccountResponse
import com.egobook.app.data.model.account.LinkRequest
import com.egobook.app.data.model.account.LinkResponse
import com.egobook.app.data.model.account.WithdrawReasonRequest
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import com.egobook.app.data.model.account.NicknameRequest

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

    // 탈퇴 사유 저장. 반드시 deleteAccount() 호출 이전에 호출해야 저장된다.
    @POST("/users/withdraw/reason")
    suspend fun submitWithdrawReason(
        @Body request: WithdrawReasonRequest
    ): ApiResponse<JsonElement?>

    // data 필드가 문자열/객체/null 어느 것이든 Gson이 파싱 가능하도록 JsonElement? 사용
    @PATCH("/users/nickname")
    suspend fun updateNickname(
        @Body request: NicknameRequest
    ): ApiResponse<JsonElement?>

}