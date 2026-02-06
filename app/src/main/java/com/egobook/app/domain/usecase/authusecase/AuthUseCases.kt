package com.egobook.app.domain.usecase.authusecase

import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject


// 의존성 주입을 쉽게 하기 위한 래퍼 클래스
data class AuthUseCases @Inject constructor (
    val googleSignUp: GoogleSignUp,
    val googleAutoLogin: GoogleAutoLogin,
    val googleLogin: GoogleLogin,
    val guestLogin: GuestLogin,
    val guestReLogin: GuestReLogin
)

// 각 유스케이스들 정의
class GoogleSignUp @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.googleSignUp(idToken)
    }

}
class GoogleAutoLogin @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshAccessToken() //액세스토큰 갱신
    }

}
class GoogleLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.refreshTokens(idToken) //로그인 시에는 토큰들을 다 갱신!!
    }
}

class GuestLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): Result<Unit> {
        return repository.guestLogin()
    }
}

class GuestReLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshGuestTokens() //토큰들 갱신
    }
}



