package com.egobook.app.data.repository.account

import com.egobook.app.data.api.AccountApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.account.LinkRequest
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.data.util.safeAuthApiCall
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.domain.repository.account.LinkedAccountInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class AccountRepositoryImpl @Inject constructor(
    private val apiService: AccountApiService,
    private val userInfoStorage: UserInfoStorage
) : AccountRepository {

    override suspend fun getUserId(forceRefresh: Boolean): Result<String> {

        //datastore에 데이터가 있는지 먼저 체크
        val localUserId = userInfoStorage.getUserId().firstOrNull()

        //datastore에 데이터가 있고 갱신을 강제하지 않는다면 바로 리턴
        if (!localUserId.isNullOrBlank() && !forceRefresh) {
            return Result.success(localUserId)
        }

        //datastore에 데이터가 없다면 api호출
        val result = safeApiCall(
            apiCall = { apiService.getUserId() },
            transform = { it.accountCode }
        )

        //응답 값을 캐싱
        result.onSuccess { userId ->
            userInfoStorage.saveUserId(userId)
        }

        return result
    }

    override suspend fun linkToGoogle(idToken: String): Result<Unit> {
        // 현재 로그인 타입이 GUEST인지 체크
        val currentLoginType = userInfoStorage.getLoginType().firstOrNull()
        
        // GUEST가 아니면 에러 반환
        if (currentLoginType != UserInfoStorage.LoginType.GUEST) {
            return Result.failure(Exception("게스트 계정만 구글 연동이 가능합니다."))
        }
        
        // idToken 전송 (API 호출) 및 토큰 저장
        return safeAuthApiCall(
            apiCall = {
                apiService.linkToGoogle(LinkRequest(idToken = idToken))
            }
        ).map { tokenData ->
            // Access, Refresh Token 저장 (Recover Token은 null로 설정하여 저장하지 않음)
            userInfoStorage.saveAllTokens(
                accessToken = tokenData.accessToken,
                refreshToken = tokenData.refreshToken,
                recoverToken = null
            )
            
            // 로그인 타입을 GOOGLE로 변경
            val loginType = UserInfoStorage.LoginType.GOOGLE
            userInfoStorage.saveLoginType(loginType)

            // 이메일 저장
            userInfoStorage.saveUserEmail(tokenData.email)
            Timber.d("구글 로그인 성공, loginType=$loginType, email=${tokenData.email}")
        }
    }

    override suspend fun getLinkedAccountInfo(): Result<LinkedAccountInfo> {
        val loginType = userInfoStorage.getLoginType().firstOrNull()
        val isGoogleLinked = loginType == UserInfoStorage.LoginType.GOOGLE
        val email = userInfoStorage.getUserEmail().firstOrNull()

        return Result.success(
            LinkedAccountInfo(
                email = email,
                isGoogleLinked = isGoogleLinked
            )
        )
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val result = safeApiCall(
                apiCall = { apiService.deleteAccount() },
                transform = { Unit }
            )

            result.onSuccess {
                try {
                    //datastore의 모든 데이터 삭제
                    userInfoStorage.clearAll()
                    Timber.d("회원 탈퇴 성공: UserInfoStorage 초기화 완료")
                } catch (e: Exception) {
                    Timber.e(e, "회원 탈퇴 후 UserInfoStorage 초기화 실패")
                }
            }

            result
        } catch (e: Exception) {
            Timber.e(e, "회원 탈퇴 처리 중 예외 발생")
            Result.failure(e)
        }
    }


}