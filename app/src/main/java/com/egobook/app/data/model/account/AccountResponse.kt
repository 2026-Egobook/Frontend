package com.egobook.app.data.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    @SerialName("accountCode")
    val accountCode: String,
)
