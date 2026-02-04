package com.egobook.app.data.repository

import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.DetectAbusiveContentRequest
import com.egobook.app.data.model.square.letter.ReplyLetterRequest
import com.egobook.app.data.model.square.letter.toData
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetterItem
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.repository.LetterRepository
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.ReplyLetter
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
        Result.success(mockData)
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
}