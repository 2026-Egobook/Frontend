package com.egobook.app.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//Google 최초 회원가입
@Serializable
data class TokenResponseByGoogle(
    @SerialName("status")
    val status: Int,
    
    @SerialName("code")
    val code: String,
    
    @SerialName("message")
    val message: String,
    
    @SerialName("data")
    val data: TokenData
)

//Guest 최초 둘러보기
@Serializable
data class TokenResponseByGuest(
    @SerialName("status")
    val status: Int,

    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: GuestTokenData
)

//액세스토큰 재발급
@Serializable
data class AccessTokenResponse(
    @SerialName("status")
    val status: Int,

    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: TokenData
)

////Tokens 재발급 - refreshToken까지 만료시
@Serializable
data class TokensResponse(
    @SerialName("status")
    val status: Int,

    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: TokenData
)

//Guest로그인 상태에서 Token 재발급
@Serializable
data class TokenResponseAgainByGuest(
    @SerialName("status")
    val status: Int,

    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: GuestTokenData

)


//===============================================================

@Serializable
data class TokenData(
    @SerialName("accessToken")
    val accessToken: String,
    
    @SerialName("refreshToken")
    val refreshToken: String
)

@Serializable
data class GuestTokenData(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("recoverToken")
    val recoverToken: String
)

