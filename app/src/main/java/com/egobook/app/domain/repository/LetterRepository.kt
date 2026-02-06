package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.model.square.letter.SentLetterItem
import com.egobook.app.domain.model.square.letter.SentLetterWithReply
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    suspend fun sendLetter(letter: SendLetter): Result<Unit>
    suspend fun detectAbusiveContent(text: String): Result<AbusiveContentAnalysis>
    suspend fun fetchArrivedPendingLetter(): Result<ArrivedPendingLetter>
    suspend fun replyLetter(letterId: Long, text: String): Result<ReplyLetter>
    suspend fun deferReplyLetter(letterId: Long): Result<Unit>
    suspend fun giveUpReplyLetter(letterId: Long): Result<Unit>
    fun fetchSentLetters(size: Int): Flow<PagingData<SentLetterItem>>
    suspend fun fetchSentLetterWithReply(letterId: Long): Result<SentLetterWithReply>
}