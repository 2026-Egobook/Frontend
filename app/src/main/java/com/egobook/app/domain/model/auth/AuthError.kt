package com.egobook.app.domain.model.auth

sealed class AuthError(
    override val message: String? = null
) : Throwable(message) {

    //400
    class BadRequest :
        AuthError("잘못된 요청입니다.")

    //401
    class InvalidCredentials :
        AuthError("유효하지 않은 계정입니다.")

    //403
    class WaitDelete :
        AuthError("탈퇴 처리 중인 계정입니다. 관리자에게 문의하세요.")

    //409
    class UserAlreadyExists :
        AuthError("이미 가입된 구글 계정입니다.")

    //404
    class UserNotFound :
        AuthError("존재하지 않는 사용자입니다.")

    //미정
    class NetworkError :
        AuthError("네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")

    //else
    class Unknown(message: String?) :
        AuthError(message)
}