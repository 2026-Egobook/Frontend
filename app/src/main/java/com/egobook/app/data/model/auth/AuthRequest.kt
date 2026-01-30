package com.egobook.app.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//Google 최초 회원가입
@Serializable
data class TokenRequestByGoogle(
    @SerialName("idToken")
    val idToken: String
)

//Guest 최초 둘러보기
@Serializable
data class TokenRequestByGuest(
    @SerialName("deviceUid")
    val deviceUid: String
)

//액세스토큰 재발급
@Serializable
data class AccessTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String
)

//Refresh Token 재발급
@Serializable
data class RefreshTokenRequest(
    @SerialName("idToken")
    val idToken: String
)

//Guest로그인 상태에서 Token 재발급
@Serializable
data class TokenRequestAgainByGuest(
    @SerialName("deviceUid")
    val deviceUid: String,

    @SerialName("recoverToken")
    val recoverToken: String,

)

