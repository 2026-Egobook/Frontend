package com.egobook.app.domain.repository

import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.SendLetter

interface LetterRepository {
    suspend fun sendLetter(letter: SendLetter): Result<Unit>
    suspend fun detectAbusiveContent(text: String): Result<AbusiveContentAnalysis>
    suspend fun fetchArrivedPendingLetter(): Result<ArrivedPendingLetter>
    suspend fun replyLetter(letterId: Long, text: String): Result<ReplyLetter>
}