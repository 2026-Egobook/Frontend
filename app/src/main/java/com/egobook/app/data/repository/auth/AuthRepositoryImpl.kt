package com.egobook.app.data.repository.auth

import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.data.model.auth.TokenRequestByGoogle
import com.egobook.app.data.model.auth.TokenRequestByGuest
import com.egobook.app.data.model.auth.TokensRequest
import com.egobook.app.data.model.auth.TokensRequestAgainByGuest
import com.egobook.app.data.util.safeApiCallWithSuspendTransform
import com.egobook.app.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userInfoStorage: UserInfoStorage,
) : AuthRepository {

    override suspend fun googleSignUp(idToken: String): Result<Unit> {
        return safeApiCallWithSuspendTransform(
            apiCall = {
                apiService.googleSignUp(
                    TokenRequestByGoogle(idToken = idToken)
                )
            },
            transform = { tokenData ->
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                val loginType = UserInfoStorage.LoginType.GOOGLE
                userInfoStorage.saveLoginType(loginType)

                //유저 이메일 저장
                userInfoStorage.saveUserEmail(tokenData.email)
                Timber.d("구글 로그인 성공, loginType=$loginType, email=${tokenData.email}")
                Unit
            }
        )
    }

    override suspend fun guestLogin(): Result<Unit> {
        // 앱 설치 인스턴스 고유 UUID
        val deviceUid = UUID.randomUUID().toString()
        
        Timber.d("게스트 로그인 시도: deviceUid=$deviceUid")
        
        return safeApiCallWithSuspendTransform(
            apiCall = {
                apiService.guestLogin(
                    TokenRequestByGuest(deviceUid = deviceUid)
                )
            },
            transform = { tokenData ->
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
                Unit
            }
        ).also { result ->
            if (result.isFailure) {
                Timber.e(result.exceptionOrNull(), "게스트 로그인 중 오류")
            }
        }
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        // Access, Refresh Token 읽기
        val accessToken = userInfoStorage.getAccessToken().first()
            ?: return Result.failure(Exception("액세스 토큰을 찾을 수 없습니다."))

        val refreshToken = userInfoStorage.getRefreshToken().first()
            ?: return Result.failure(Exception("리프레시 토큰을 찾을 수 없습니다."))

        Timber.d("액세스 토큰 재발급 시도 시작")

        return safeApiCallWithSuspendTransform(
            apiCall = {
                apiService.getAccessToken(
                    AccessTokenRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            },
            transform = { tokenData ->
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                Timber.d("액세스 토큰 재발급 성공")
                Unit
            }
        ).also { result ->
            if (result.isFailure) {
                Timber.e(result.exceptionOrNull(), "액세스 토큰 재발급 중 오류")
            }
        }
    }

    //구글 로그인 시 사용
    override suspend fun refreshTokens(idToken: String): Result<Unit> {
        // 액세스 토큰 가져오기 (없으면 null)
        val accessToken = userInfoStorage.getAccessToken().first()

        return safeApiCallWithSuspendTransform(
            apiCall = {
                apiService.reGetTokens(
                    TokensRequest(
                        idToken = idToken,
                        accessToken = accessToken
                    )
                )
            },
            transform = { tokenData ->
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                //로그인 타입 저장
                val loginType = UserInfoStorage.LoginType.GOOGLE
                userInfoStorage.saveLoginType(loginType)
                //유저 이메일 저장
                userInfoStorage.saveUserEmail(tokenData.email)
                Timber.d("구글 로그인 성공, loginType=$loginType, email=${tokenData.email}")
                Unit
            }
        )
    }

    override suspend fun refreshGuestTokens(): Result<Unit> {
        // Device UID 및 Access Token, Recover Token 읽기
        val deviceUid = userInfoStorage.getDeviceUid().first()
            ?: return Result.failure(Exception("디바이스 UID를 찾을 수 없습니다."))

        val accessToken = userInfoStorage.getAccessToken().first()
            ?: return Result.failure(Exception("accessToken을 찾을 수 없습니다."))

        val recoverToken = userInfoStorage.getRecoverToken().first()
            ?: return Result.failure(Exception("recoverToken을 찾을 수 없습니다."))

        Timber.d("게스트 토큰 재발급 시도")

        return safeApiCallWithSuspendTransform(
            apiCall = {
                apiService.reGetTokensByGuest(
                    TokensRequestAgainByGuest(
                        deviceUid = deviceUid,
                        accessToken = accessToken,
                        recoverToken = recoverToken
                    )
                )
            },
            transform = { tokenData ->
                userInfoStorage.saveAllTokens(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken,
                    recoverToken = tokenData.recoverToken
                )
                Timber.d("게스트 토큰 재발급 성공")
                Unit
            }
        ).also { result ->
            if (result.isFailure) {
                Timber.e(result.exceptionOrNull(), "게스트 토큰 재발급 중 오류")
            }
        }
    }

}