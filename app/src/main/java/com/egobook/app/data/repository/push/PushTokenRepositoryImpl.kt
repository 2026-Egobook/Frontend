package com.egobook.app.data.repository.push

import com.egobook.app.BuildConfig
import com.egobook.app.data.api.PushApiService
import com.egobook.app.data.local.PushPreferenceStorage
import com.egobook.app.data.model.push.FcmTokenRequest
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.domain.repository.push.PushTokenRepository
import com.egobook.app.push.PushTokenRegistrationPolicy
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PushTokenRepositoryImpl
    @Inject
    constructor(
        private val apiService: PushApiService,
        private val pushPreferenceStorage: PushPreferenceStorage,
    ) : PushTokenRepository {
        override suspend fun registerCurrentToken(): Result<Unit> {
            val currentToken =
                runCatching { fetchCurrentToken() }
                    .getOrElse { error ->
                        Timber.e(error, "FCM 토큰 조회 실패")
                        return Result.failure(error)
                    }

            // 콘솔에서 테스트 메시지를 보낼 때 토큰이 필요하므로 디버그 빌드에서만 출력한다.
            if (BuildConfig.DEBUG) Timber.d("FCM token: %s", currentToken)

            val lastRegisteredToken = pushPreferenceStorage.getLastRegisteredToken()
            if (!PushTokenRegistrationPolicy.shouldRegister(currentToken, lastRegisteredToken)) {
                Timber.d("이미 등록된 FCM 토큰이므로 서버 호출을 생략합니다.")
                return Result.success(Unit)
            }

            return safeApiCall(
                apiCall = { apiService.updateFcmToken(FcmTokenRequest(currentToken)) },
                transform = { Unit },
            ).onSuccess {
                pushPreferenceStorage.saveLastRegisteredToken(currentToken)
                Timber.d("FCM 토큰 등록 성공")
            }.onFailure { error ->
                Timber.e(error, "FCM 토큰 등록 실패")
            }
        }

        private suspend fun fetchCurrentToken(): String =
            suspendCancellableCoroutine { continuation ->
                FirebaseMessaging
                    .getInstance()
                    .token
                    .addOnSuccessListener { token -> continuation.resume(token) }
                    .addOnFailureListener { error -> continuation.resumeWithException(error) }
            }
    }
