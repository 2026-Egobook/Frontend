package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.account.AccountRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(private val repository: AccountRepository) {
    suspend operator fun invoke(): Result<String> {
        return repository.getUserId()
    }
}