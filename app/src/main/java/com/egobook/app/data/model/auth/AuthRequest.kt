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
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String
)

//Tokens 재발급 - refreshToken까지 만료시
@Serializable
data class TokensRequest(
    @SerialName("idToken")
    val idToken: String,
    val accessToken: String? = null //디폴트는 null로
)

//Guest로그인 상태에서 Tokens 재발급
@Serializable
data class TokensRequestAgainByGuest(
    @SerialName("deviceUid")
    val deviceUid: String,

    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("recoverToken")
    val recoverToken: String,

)

