package com.egobook.app.ui.home.repository

data class BaseResponse<T>(
    val code: String,
    val message: String,
    val status: Int,
    val data: T
)
