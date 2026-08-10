package com.egobook.app.data.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    @SerialName("fcmToken")
    val fcmToken: String,
)
