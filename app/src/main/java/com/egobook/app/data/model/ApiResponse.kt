package com.egobook.app.data.model

import com.egobook.app.data.model.diary.response.EmptyResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("status")
    val status: Int,

    @SerialName("data")
    val data: T
)

@Serializable
data class ApiResponseEmpty(
    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String,

    @SerialName("status")
    val status: Int,

    @SerialName("data")
    val data: EmptyResponse = EmptyResponse()
)