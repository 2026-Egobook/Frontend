package com.egobook.app.data.repository

import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.toData
import com.egobook.app.domain.model.square.letter.SendLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class LetterRepositoryImpl @Inject constructor(private val apiService: LetterApiService): LetterRepository {
    override suspend fun sendLetter(letter: SendLetter): Result<Unit> = try {
        val response = apiService.sendLetter(request = letter.toData())
        if(response.status == 200) {
            Result.success(Unit) // 나중에 구현하면서 응답 필요할 때 변경
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}