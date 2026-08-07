package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateMarketingConsentUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = repository.updateMarketingConsent(enabled = enabled)
}
