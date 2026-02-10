package com.egobook.app.data.repository.account

import com.egobook.app.data.api.AccountApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.domain.repository.account.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class AccountRepositoryImpl @Inject constructor(
    private val apiService: AccountApiService,
    private val userInfoStorage: UserInfoStorage
) : AccountRepository {

    override suspend fun getUserId(): Result<String> {

        //datastore에 데이터가 있는지 먼저 체크
        val localUserId = userInfoStorage.getUserId().firstOrNull()

        //있다면 바로 리턴
        if (!localUserId.isNullOrBlank()) {
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

}