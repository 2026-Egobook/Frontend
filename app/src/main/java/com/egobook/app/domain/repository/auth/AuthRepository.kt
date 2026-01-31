package com.egobook.app.domain.repository.auth

interface AuthRepository {

    suspend fun googleSignUp(): Result<Unit>

    suspend fun guestLogin(): Result<Unit>

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun refreshTokens(): Result<Unit>

    suspend fun refreshGuestTokens(): Result<Unit>

}