package com.egobook.app.data.repository.auth

import android.util.Log
import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.data.model.auth.TokenRequestAgainByGuest
import com.egobook.app.data.model.auth.TokensRequest
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
        return try {
            // Refresh Token 읽기
            val refreshToken = userInfoStorage.getRefreshToken().first()
                ?: return Result.failure(Exception("리프레시 토큰을 찾을 수 없습니다."))

            Log.d(TAG, "액세스 토큰 재발급 시도")

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

    override suspend fun refreshTokens(): Result<Unit> {
        return try {
            // ID Token 읽기
            val idToken = userInfoStorage.getIdToken().first()
                ?: return Result.failure(Exception("ID 토큰을 찾을 수 없습니다."))

            Log.d(TAG, "토큰 재발급 시도 (리프레시 토큰 만료")

            // API 요청
            val response = apiService.reGetTokens(
                TokensRequest(idToken = idToken)
            )

            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 성공 여부: ${response.isSuccessful}")

            // 응답 성공시
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken,
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("토큰 재발급 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "토큰 재발급 중 오류", e)
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
                TokenRequestAgainByGuest(
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