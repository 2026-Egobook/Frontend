package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class GiveUpReplyLetterUseCase @Inject constructor(
    private val repository: LetterRepository
) {
    suspend operator fun invoke(letterId: Long): Result<Unit> = repository.giveUpReplyLetter(letterId = letterId)
}