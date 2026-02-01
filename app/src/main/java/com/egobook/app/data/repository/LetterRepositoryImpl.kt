package com.egobook.app.data.repository

import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.DetectAbusiveContentRequest
import com.egobook.app.data.model.square.letter.toData
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class LetterRepositoryImpl @Inject constructor(
    private val letterApiService: LetterApiService,
    private val aiApiService: AIApiService
): LetterRepository {
    override suspend fun sendLetter(letter: SendLetter): Result<Unit> = try {
        val response = letterApiService.sendLetter(request = letter.toData())
        if(response.status == 200) {
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
}