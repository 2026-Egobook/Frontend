package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class SendLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(letter: SendLetter): Result<Unit> = repository.sendLetter(letter = letter)
}