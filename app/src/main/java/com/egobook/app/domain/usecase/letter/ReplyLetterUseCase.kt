package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class ReplyLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(letterId: Long, text: String, backgroundColor: LetterBackgroundColor): Result<ReplyLetter> = repository.replyLetter(letterId, text, backgroundColor)
}