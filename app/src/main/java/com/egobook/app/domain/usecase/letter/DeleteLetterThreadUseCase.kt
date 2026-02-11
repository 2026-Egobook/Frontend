package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class DeleteLetterThreadUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(threadId: Long): Result<Unit> = repository.deleteLetterThread(threadId = threadId)
}