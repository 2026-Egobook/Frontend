package com.egobook.app.domain.repository.auth

import com.egobook.app.domain.model.User

interface AuthRepository {

    suspend fun googleSignUp(idToken: String): Result<User>

    suspend fun guestLogin(deviceUid: String): Result<User>

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun refreshTokens(idToken: String): Result<Unit>

    suspend fun refreshGuestTokens(deviceUid: String, recoverToken: String): Result<Unit>

    suspend fun logout()

    suspend fun getCurrentUser(): User?
}