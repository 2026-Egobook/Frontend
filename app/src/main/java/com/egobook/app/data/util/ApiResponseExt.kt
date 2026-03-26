package com.egobook.app.data.util

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.ApiResponseEmpty
import com.egobook.app.domain.model.auth.AuthError
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


/**
 * 로그인/회원가입 전용 에러 매핑 함수
 */
fun ApiResponse<*>.toAuthError(): AuthError {
    Timber.d("toAuthError called with status: $status, message: $message")
    return when (this.status) {
        400 -> AuthError.BadRequest()
        401 -> AuthError.InvalidCredentials()
        403 -> AuthError.WaitDelete()
        404 -> AuthError.UserNotFound()
        409 -> AuthError.UserAlreadyExists()
        else -> AuthError.Unknown(this.message)
    }
}

/**
 * 로그인/회원가입 전용 Result로 변환 확장 함수
 */
fun <T> ApiResponse<T>.toAuthResult(): Result<T> {
    return if (this.status == 200) {
        Result.success(this.data)
    } else {
        Result.failure(this.toAuthError())
    }
}


/**
 * ApiResponse를 Result로 변환하는 확장 함수
 */
inline fun <T, R> ApiResponse<T>.toResult(
    transform: (T) -> R
): Result<R> {
    return if (this.status == 200) {
        Result.success(transform(this.data))
    } else {
        Result.failure(Exception(this.message))
    }
}

/**
 * ApiResponse를 Result로 변환 (변환 없이)
 */
fun <T> ApiResponse<T>.toResult(): Result<T> {
    return if (this.status == 200) {
        Result.success(this.data)
    } else {
        Result.failure(Exception(this.message))
    }
}

/**
 * (로그인/회원가입 전용) API 호출을 안전하게 실행하는 헬퍼 함수
 */
suspend fun <T> safeAuthApiCall(
    apiCall: suspend () -> ApiResponse<T>
): Result<T> {
    return try {
        apiCall().toAuthResult()
    } catch (e: HttpException) {
        val statusCode = e.code()
        Timber.d("HttpException caught with code: $statusCode")
        val authError = when (statusCode) {
            400 -> AuthError.BadRequest()
            401 -> AuthError.InvalidCredentials()
            403 -> AuthError.WaitDelete()
            404 -> AuthError.UserNotFound()
            409 -> AuthError.UserAlreadyExists()
            else -> AuthError.Unknown(e.message)
        }
        Result.failure(authError)
    } catch (e: IOException) {
        Result.failure(AuthError.NetworkError())
    } catch (e: Exception) {
        Result.failure(AuthError.Unknown(e.message))
    }
}

/**
 * API 호출을 안전하게 실행하는 헬퍼 함수
 */
//suspend fun <T> safeApiCall(
//    apiCall: suspend () -> Unit
//): Result<T> {
//    return try {
//        apiCall().toResult()
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//}


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
 * API 호출을 안전하게 실행하고 변환하는 헬퍼 함수 (suspend transform 지원)
 * transform 내부에서 suspend 함수를 호출할 수 있도록 지원
 */
suspend inline fun <T, R> safeApiCallWithSuspendTransform(
    crossinline apiCall: suspend () -> ApiResponse<T>,
    crossinline transform: suspend (T) -> R
): Result<R> {
    return try {
        val response = apiCall()
        if (response.status == 200) {
            Result.success(transform(response.data))
        } else {
            Result.failure(response.toAuthError())
        }
    } catch (e: IOException) {
        Result.failure(AuthError.NetworkError())
    } catch (e: Exception) {
        Result.failure(AuthError.Unknown(e.message))
    }
}

/**
 * 의미있는 데이터를 반환하지 않는 API 응답을 처리하는 전용 확장 함수
 */

fun ApiResponseEmpty.toResult(): Result<Unit> {
    return if (this.status == 200) {
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
