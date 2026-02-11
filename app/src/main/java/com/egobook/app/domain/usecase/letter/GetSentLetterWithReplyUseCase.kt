package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class GetSentLetterWithReplyUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(letterId: Long) = repository.fetchSentLetterWithReply(letterId)
}
