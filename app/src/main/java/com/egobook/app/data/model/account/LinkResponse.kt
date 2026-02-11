package com.egobook.app.data.model.account

import kotlinx.serialization.SerialName

data class LinkResponse(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("email")
    val email: String,
)
