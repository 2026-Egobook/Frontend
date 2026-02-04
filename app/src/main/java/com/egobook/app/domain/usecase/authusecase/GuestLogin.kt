package com.egobook.app.domain.usecase.authusecase

import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GuestLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): Result<Unit> {
        return repository.guestLogin()
    }
}