package com.example.egobook_frontent.data.repository

import com.example.egobook_frontent.data.api.CounselingApi
import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(private val api: CounselingApi): CounselingRepository {
    override suspend fun getDailyPraise(): Result<List<PraiseMessage>> = try {
        val response = api.fetchDailyPraise()
        if(response.isSuccessful && response.body() != null) {
            val domainList = response.body()!!.map { it.toDomain() }
            Result.success(domainList)
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}