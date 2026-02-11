package com.egobook.app.data.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkRequest(
    @SerialName("idToken")
    val idToken: String
)
