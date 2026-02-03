package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class GetArrivedPendingLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke():Result<ArrivedPendingLetter> = repository.fetchArrivedPendingLetter()
}