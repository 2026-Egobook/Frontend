package com.egobook.app.domain.repository.auth

interface AuthRepository {

    suspend fun googleSignUp(): Result<Unit>

    suspend fun guestLogin(deviceUid: String): Result<Unit>

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun refreshTokens(idToken: String): Result<Unit>

    suspend fun refreshGuestTokens(deviceUid: String, recoverToken: String): Result<Unit>

}