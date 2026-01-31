package com.egobook.app.data.repository.auth

import android.util.Log
import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userInfoStorage: UserInfoStorage
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun googleSignUp(): Result<Unit> {
        return try {
            // ID 토큰 읽기
            val idToken = userInfoStorage.getIdToken().first()
                ?: return Result.failure(Exception("발급받은 ID 토큰을 찾을 수 없습니다."))

            Log.d(TAG, "ID Token 길이: ${idToken.length}")
            Log.d(TAG, "ID Token 시작부분: ${idToken.take(50)}...")

            // API 요청
            val response = apiService.googleSignUp(
                TokenRequestByGoogle(idToken = idToken)
            )

            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 성공 여부: ${response.isSuccessful}")

            if (response.body() != null) {
                val body = response.body()!!
                Log.d(TAG, "응답 상태: ${body.status}")
                Log.d(TAG, "응답 코드: ${body.code}")
                Log.d(TAG, "응답 메시지: ${body.message}")
            }

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
            Log.e(TAG, "API 호출 중 오류", e)
            Result.failure(e)
        }
    }

    override suspend fun guestLogin(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTokens(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshGuestTokens(): Result<Unit> {
        TODO("Not yet implemented")
    }

}