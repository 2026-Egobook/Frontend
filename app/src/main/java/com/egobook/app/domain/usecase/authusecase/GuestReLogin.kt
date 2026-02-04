package com.egobook.app.domain.usecase.authusecase

import com.egobook.app.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GuestReLogin @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshGuestTokens()
    }
}