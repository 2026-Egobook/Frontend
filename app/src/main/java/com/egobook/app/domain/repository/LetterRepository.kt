package com.egobook.app.domain.repository

import com.egobook.app.domain.model.square.letter.SendLetter

interface LetterRepository {
    suspend fun sendLetter(letter: SendLetter): Result<Unit>
}