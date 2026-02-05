package com.egobook.app.domain.usecase.authusecase

import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GoogleSignUp @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.googleSignUp(idToken)
    }

}