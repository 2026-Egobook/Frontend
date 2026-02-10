package com.egobook.app.data.util

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.ApiResponseEmpty


/**
 * ApiResponse를 Result로 변환하는 확장 함수
 */
inline fun <T, R> ApiResponse<T>.toResult(
    transform: (T) -> R
): Result<R> {
    return if (this.code == "SUCCESS") {
        Result.success(transform(this.data))
    } else {
        Result.failure(Exception(this.message))
    }
}

/**
 * ApiResponse를 Result로 변환 (변환 없이)
 */
fun <T> ApiResponse<T>.toResult(): Result<T> {
    return if (this.code == "SUCCESS") {
        Result.success(this.data)
    } else {
        Result.failure(Exception(this.message))
    }
}

/**
 * API 호출을 안전하게 실행하는 헬퍼 함수
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> ApiResponse<T>
): Result<T> {
    return try {
        apiCall().toResult()
    } catch (e: Exception) {
        Result.failure(e)
    }
}


/**
 * API 호출을 안전하게 실행하고 변환하는 헬퍼 함수
 */
suspend inline fun <T, R> safeApiCall(
    crossinline apiCall: suspend () -> ApiResponse<T>,
    crossinline transform: (T) -> R
): Result<R> {
    return try {
        apiCall().toResult(transform)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * 의미있는 데이터를 반환하지 않는 API 응답을 처리하는 전용 확장 함수
 */

fun ApiResponseEmpty.toResult(): Result<Unit> {
    return if (this.code == "SUCCESS") {
        Result.success(Unit)
    } else {
        Result.failure(Exception(this.message))
    }
}

/**
 * 빈 응답 API 호출을 안전하게 실행하는 헬퍼 함수
 */
suspend fun safeApiCallEmpty(
    apiCall: suspend () -> ApiResponseEmpty
): Result<Unit> {
    return try {
        apiCall().toResult()
    } catch (e: Exception) {
        Result.failure(e)
    }
}