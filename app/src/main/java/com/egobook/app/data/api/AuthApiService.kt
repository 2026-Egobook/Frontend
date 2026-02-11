package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.data.model.auth.GuestTokenData
import com.egobook.app.data.model.auth.TokenData
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.data.model.auth.TokenRequestByGuest
import com.egobook.app.data.model.auth.TokensRequest
import com.egobook.app.data.model.auth.TokensRequestAgainByGuest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    //Google 최초 회원가입
    @POST("auth/google/join")
    suspend fun googleSignUp(
        @Body request: TokenRequestByGoogle
    ): ApiResponse<TokenData>

    //Guest 최초 둘러보기
    @POST("auth/guest/join")
    suspend fun guestLogin(
        @Body request: TokenRequestByGuest
    ): ApiResponse<GuestTokenData>

    //액세스토큰 재발급
    @POST("auth/refresh")
    suspend fun getAccessToken(
        @Body request: AccessTokenRequest
    ): ApiResponse<TokenData>

    //Tokens 재발급 - refreshToken까지 만료시
    @POST("auth/google/recertification")
    suspend fun reGetTokens(
        @Body request: TokensRequest
    ): ApiResponse<TokenData>

    //Guest로그인 상태에서 Tokens 재발급
    @POST("auth/google/recertification")
    suspend fun reGetTokensByGuest(
        @Body request: TokensRequestAgainByGuest
    ): ApiResponse<GuestTokenData>

}