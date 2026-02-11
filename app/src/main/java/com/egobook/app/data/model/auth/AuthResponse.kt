package com.egobook.app.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 표준 Google 로그인/회원가입 토큰 응답 데이터
 * ApiResponse<TokenData> 형태로 사용됨
 */
@Serializable
data class TokenData(
    @SerialName("accessToken")
    val accessToken: String,
    
    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("email")
    val email: String,
)

/**
 * Guest 로그인 토큰 응답 데이터 (recoverToken 포함)
 * ApiResponse<GuestTokenData> 형태로 사용됨
 */
@Serializable
data class GuestTokenData(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("recoverToken")
    val recoverToken: String
)

