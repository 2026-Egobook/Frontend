package com.egobook.app.data.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawReasonRequest(
    @SerialName("reasonType")
    val reasonType: String,

    @SerialName("text")
    val text: String? = null,
)
