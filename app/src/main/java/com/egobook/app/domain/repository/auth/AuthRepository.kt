package com.egobook.app.domain.repository.auth

interface AuthRepository {

    suspend fun googleSignUp(idToken: String): Result<Unit>

    suspend fun googleLogin(idToken: String): Result<Unit>

    suspend fun guestLogin(): Result<Unit>

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun refreshTokens(idToken: String): Result<Unit>

    suspend fun refreshGuestTokens(): Result<Unit>

}