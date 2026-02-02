package com.egobook.app.domain.usecase.authusecase

import javax.inject.Inject


// 의존성 주입을 쉽게 하기 위한 래퍼 클래스
data class AuthUseCases @Inject constructor (
    val googleSignUp: GoogleSignUp,
    val googleAutoLogin: GoogleAutoLogin,
    val googleLogin: GoogleLogin
)