package com.egobook.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.ReplyLetterRequest
import com.egobook.app.data.model.square.letter.toData
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.data.repository.paging.SentLettersPagingSource
import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetterItem
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterReply
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.ReportLetter
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
//        val response = aiApiService.detectAbusiveContent(request = DetectAbusiveContentRequest(text = text))
//        if(response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        val dummyData = AbusiveContentAnalysis(
            text = "테스트 중입니다",
            riskScore = 40.0,
            isHarmful = true,
            label = "응?",
            detectedBadWords = emptyList()
        )
        Result.success(dummyData)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchArrivedPendingLetter(): Result<ArrivedPendingLetter> = try {
//        val response = letterApiService.fetchArrivedPendingLetter()
//        if(response.status == 200) {
//            Result.success(response.data.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val mockData = ArrivedPendingLetter(
            letter = ArrivedPendingLetterItem(
                letterId = 1L,
                status = LetterStatus.ARRIVED,
                mode = LetterMode.RANDOM,
                fromLabel = "익명",
                content = "안녕하세요! 요즘 날씨가 부쩍 추워졌는데 잘 지내고 계신가요? 오늘 우연히 당신의 이야기를 듣고 문득 위로의 말을 전하고 싶어 펜을 들었습니다. 누구나 가끔은 마음이 무겁고 모든 게 버겁게 느껴지는 날이 있잖아요. 그럴 때일수록 스스로를 너무 다그치지 말고, 따뜻한 차 한 잔 마시며 쉬어갔으면 좋겠어요. 당신은 충분히 잘해내고 있고, 존재만으로도 소중한 사람이라는 걸 잊지 마세요. 내일은 오늘보다 조금 더 웃을 수 있는 여유가 생기길 진심으로 응원하겠습니다! 답장 기다릴게요.",
                arrivedAt = "2026-02-02T10:35:00+09:00",
                replyDeadlineAt = "2026-02-04T21:40:00+09:00",
                letterColor = LetterBackgroundColor.BEIGE
            )
        )
        val emptyMockData = ArrivedPendingLetter(
            letter = null
        )
        Result.success(emptyMockData)
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
//        val response = letterApiService.fetchSentLetterWithReply(letterId = letterId)
//        if(response.status == 200) {
//            Result.success(response.data.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val mockSentLetterWithReply = SentLetterWithReply(
            letterId = letterId,
            threadId = letterId,
            status = LetterStatus.ARRIVED, // 답장이 도착함
            mode = LetterMode.RANDOM,
            sentContent = "안녕하세요, 고민이 있어 편지를 보냅니다. 요즘 업무량이 너무 많아서 번아웃이 온 것 같아요. 어떻게 극복하면 좋을까요?",
            backgroundColor = LetterBackgroundColor.BEIGE,
            createdAt = "2026-02-06T03:42:43.162307Z",
            arrivedAt = "2026-02-06T03:42:43.162307Z",
            // 답장 데이터
            reply = LetterReply(
                replyId = 101L,
                replyContent = "보내주신 편지 잘 읽었습니다. 번아웃 때문에 많이 힘드시겠어요. 저도 비슷한 경험이 있었는데, 그럴 땐 완벽하게 해내려는 마음을 조금 내려놓고 하루에 딱 10분이라도 온전히 자신만을 위해 산책을 하는 게 큰 도움이 되더라고요. 당신은 이미 충분히 잘하고 있습니다. 너무 스스로를 몰아세우지 마세요.",
                isAIGenerated = true,
                isReported = false,
                repliedAt = "2026-02-06T03:42:43.162307Z"
            )
        )
        Result.success(mockSentLetterWithReply)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun reportRepliedLetter(replyId: Long, reportLetter: ReportLetter): Result<Unit> = try {
        val response = letterApiService.reportRepliedLetter(replyId = replyId, request = reportLetter.toData())
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}