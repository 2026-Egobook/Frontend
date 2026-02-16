package com.egobook.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.DetectAbusiveContentRequest
import com.egobook.app.data.model.square.letter.ReplyLetterRequest
import com.egobook.app.data.model.square.letter.toData
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.data.repository.paging.DeferredLettersPagingSource
import com.egobook.app.data.repository.paging.SentLettersPagingSource
import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.DeferredLetter
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.model.square.letter.SentLetterItem
import com.egobook.app.domain.model.square.letter.SentLetterWithReply
import com.egobook.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LetterRepositoryImpl @Inject constructor(
    private val letterApiService: LetterApiService,
    private val aiApiService: AIApiService
) : LetterRepository {
    override suspend fun sendLetter(letter: SendLetter): Result<Unit> = try {
        val response = letterApiService.sendLetter(request = letter.toData())
        if (response.status == 200) {
            Result.success(Unit) // 나중에 구현하면서 응답 필요할 때 변경
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun detectAbusiveContent(text: String): Result<AbusiveContentAnalysis> = try {
        val response = aiApiService.detectAbusiveContent(request = DetectAbusiveContentRequest(text = text))
        if(response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchArrivedPendingLetter(): Result<ArrivedPendingLetter> = try {
        val response = letterApiService.fetchArrivedPendingLetter()
        if(response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun replyLetter(letterId: Long, text: String): Result<ReplyLetter> = try {
        val response = letterApiService.replyLetter(
            letterId = letterId,
            request = ReplyLetterRequest(text = text)
        )
        if (response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deferReplyLetter(letterId: Long): Result<Unit> = try {
        val response = letterApiService.deferReplyLetter(letterId = letterId)
        if (response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun giveUpReplyLetter(letterId: Long): Result<Unit> = try {
        val response = letterApiService.giveUpReplyLetter(letterId = letterId)
        if (response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun fetchSentLetters(size: Int): Flow<PagingData<SentLetterItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SentLettersPagingSource(apiService = letterApiService)
            }
        ).flow
    }

    override suspend fun fetchSentLetterWithReply(letterId: Long): Result<SentLetterWithReply> = try {
        val response = letterApiService.fetchSentLetterWithReply(letterId = letterId)
        if(response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun reportRepliedLetter(replyId: Long, reportContent: ReportContent): Result<Unit> = try {
        val response = letterApiService.reportRepliedLetter(replyId = replyId, request = reportContent.toData())
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteLetterThread(threadId: Long): Result<Unit> = try {
        val response = letterApiService.deleteLetterThread(threadId = threadId)
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun fetchDeferredLetters(size: Int): Flow<PagingData<DeferredLetter>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DeferredLettersPagingSource(apiService = letterApiService)
            }
        ).flow
    }
}