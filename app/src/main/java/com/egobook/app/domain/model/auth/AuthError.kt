package com.egobook.app.domain.model.auth

sealed class AuthError(
    override val message: String? = null
) : Throwable(message) {

    class BadRequest :
        AuthError("잘못된 요청입니다.")

    class InvalidCredentials :
        AuthError("구글 로그인을 시도할 수 없습니다. 조금 뒤에 시도해주세요")

    class UserAlreadyExists :
        AuthError("이미 가입된 계정입니다.")

    class UserNotFound :
        AuthError("존재하지 않는 사용자입니다.")

    class NetworkError :
        AuthError("네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")

    class Unknown(message: String?) :
        AuthError(message)
}