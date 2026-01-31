package com.egobook.app.data.repository.auth

import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userInfoStorage: UserInfoStorage
) : AuthRepository {
    override suspend fun googleSignUp(): Result<Unit> {
        return try {
            // ID 토큰 읽기
            val idToken = userInfoStorage.getIdToken().first()
                ?: return Result.failure(Exception("ID 토큰을 찾을 수 없습니다."))

            // API 요청
            val response = authApiService.googleSignUp(
                TokenRequestByGoogle(idToken = idToken)
            )

            //응답 성공시
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken,
                    idToken = idToken
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("회원가입 요청 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun guestLogin(deviceUid: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTokens(idToken: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshGuestTokens(
        deviceUid: String,
        recoverToken: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

}