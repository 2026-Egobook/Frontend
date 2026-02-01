package com.egobook.app.data.repository.auth

import android.util.Log
import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.data.model.auth.TokensRequest
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.data.model.auth.TokensRequestAgainByGuest
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

    override suspend fun googleSignUp(idToken: String): Result<Unit> {
        return try {
            val response = apiService.googleSignUp(
                TokenRequestByGoogle(idToken = idToken)
            )

            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data

                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                ) // ✅ idToken 저장 안 함

                Result.success(Unit)
            } else {
                Result.failure(Exception("회원가입 요청 실패: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun guestLogin(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            // Refresh Token 읽기
            val refreshToken = userInfoStorage.getRefreshToken().first()
                ?: return Result.failure(Exception("리프레시 토큰을 찾을 수 없습니다."))

            Log.d(TAG, "액세스 토큰 재발급 시도 시작")

            // API 요청
            val response = apiService.getAccessToken(
                AccessTokenRequest(refreshToken = refreshToken)
            )

            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 성공 여부: ${response.isSuccessful}")

            // 응답 성공시
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("액세스 토큰 재발급 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "액세스 토큰 재발급 중 오류", e)
            Result.failure(e)
        }
    }

    override suspend fun refreshTokens(idToken: String): Result<Unit> {
        return try {
            val response = apiService.reGetTokens(
                TokensRequest(idToken = idToken)
            )

            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data

                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )

                Result.success(Unit)
            } else {
                Result.failure(Exception("토큰 갱신 요청 실패: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshGuestTokens(): Result<Unit> {
        return try {
            // Device UID 및 Recover Token 읽기
            val deviceUid = userInfoStorage.getDeviceUid().first()
                ?: return Result.failure(Exception("디바이스 UID를 찾을 수 없습니다."))

            val recoverToken = userInfoStorage.getRecoverToken().first()
                ?: return Result.failure(Exception("recoverToken을 찾을 수 없습니다."))

            Log.d(TAG, "게스트 토큰 재발급 시도")

            // API 요청
            val response = apiService.reGetTokensByGuest(
                TokensRequestAgainByGuest(
                    deviceUid = deviceUid,
                    recoverToken = recoverToken
                )
            )

            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 성공 여부: ${response.isSuccessful}")

            // 응답 성공시
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken,
                    recoverToken = tokenData.recoverToken
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("게스트 토큰 재발급 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "게스트 토큰 재발급 중 오류", e)
            Result.failure(e)
        }
    }

}