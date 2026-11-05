package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class GetInboxLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(letterId: Long) = repository.fetchInboxLetter(letterId)
}
