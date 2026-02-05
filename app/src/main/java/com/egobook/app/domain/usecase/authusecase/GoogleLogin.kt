package com.egobook.app.domain.usecase.authusecase

import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GoogleLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.refreshTokens(idToken) //로그인 시에는 토큰들을 다 갱신!!
    }
}