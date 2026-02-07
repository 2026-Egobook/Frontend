package com.egobook.app.data.repository.auth

import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.data.model.auth.TokensRequest
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.data.model.auth.TokenRequestByGuest
import com.egobook.app.data.model.auth.TokensRequestAgainByGuest
import com.egobook.app.domain.repository.auth.AuthRepository
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import java.util.UUID

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userInfoStorage: UserInfoStorage,
) : AuthRepository {

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
                )
                val loginType = UserInfoStorage.LoginType.GOOGLE
                userInfoStorage.saveLoginType(loginType)
                Timber.d("구글 회원가입 성공, loginType=$loginType")
                Result.success(Unit)
            } else {
                Result.failure(Exception("회원가입 요청 실패: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun guestLogin(): Result<Unit> {
        return try {
            // 앱 설치 인스턴스 고유 UUID
            val deviceUid = UUID.randomUUID().toString()
            
            Timber.d("게스트 로그인 시도: deviceUid=$deviceUid")
            
            // Guest 로그인 API 요청
            val response = apiService.guestLogin(
                TokenRequestByGuest(deviceUid = deviceUid)
            )
            
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data
                
                // UUID 저장
                userInfoStorage.saveDeviceUid(deviceUid)
                
                // 토큰 저장
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken,
                    recoverToken = tokenData.recoverToken
                )

                //로그인 타입 저장
                val loginType = UserInfoStorage.LoginType.GUEST
                userInfoStorage.saveLoginType(loginType)
                Timber.d("게스트 로그인 성공, loginType=$loginType")
                Result.success(Unit)
            } else {
                Timber.e("게스트 로그인 실패: ${response.code()}")
                Result.failure(Exception("게스트 로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "게스트 로그인 중 오류")
            Result.failure(e)
        }
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            // Access, Refresh Token 읽기
            val accessToken = userInfoStorage.getAccessToken().first()
                ?: return Result.failure(Exception("액세스 토큰을 찾을 수 없습니다."))

            val refreshToken = userInfoStorage.getRefreshToken().first()
                ?: return Result.failure(Exception("리프레시 토큰을 찾을 수 없습니다."))

            Timber.d("액세스 토큰 재발급 시도 시작")

            // API 요청
            val response = apiService.getAccessToken(
                AccessTokenRequest(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )

            Timber.d("응답 코드: ${response.code()}")
            Timber.d("응답 성공 여부: ${response.isSuccessful}")

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
            Timber.e(e, "액세스 토큰 재발급 중 오류")
            Result.failure(e)
        }
    }

    override suspend fun refreshTokens(idToken: String): Result<Unit> {
        return try {
            // 액세스 토큰 가져오기 (없으면 null)
            val accessToken = userInfoStorage.getAccessToken().first()

            val response = apiService.reGetTokens(
                TokensRequest(
                    idToken = idToken,
                    accessToken = accessToken
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()!!.data

                userInfoStorage.saveAllTokens(
                    accessToken =   tokenData.accessToken,
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
            // Device UID 및 Access Token, Recover Token 읽기
            val deviceUid = userInfoStorage.getDeviceUid().first()
                ?: return Result.failure(Exception("디바이스 UID를 찾을 수 없습니다."))

            val accessToken = userInfoStorage.getAccessToken().first()
                ?: return Result.failure(Exception("accessToken을 찾을 수 없습니다."))

            val recoverToken = userInfoStorage.getRecoverToken().first()
                ?: return Result.failure(Exception("recoverToken을 찾을 수 없습니다."))

            Timber.d("게스트 토큰 재발급 시도")

            // API 요청
            val response = apiService.reGetTokensByGuest(
                TokensRequestAgainByGuest(
                    deviceUid = deviceUid,
                    accessToken = accessToken,
                    recoverToken = recoverToken
                )
            )

            Timber.d("응답 코드: ${response.code()}")
            Timber.d("응답 성공 여부: ${response.isSuccessful}")

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
            Timber.e(e, "게스트 토큰 재발급 중 오류")
            Result.failure(e)
        }
    }

}