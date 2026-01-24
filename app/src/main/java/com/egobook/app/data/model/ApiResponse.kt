package com.egobook.app.data.model

data class ApiResponse<T>(

    val code: String,

    val message: String,

    val status: Int,

    val data: T

)