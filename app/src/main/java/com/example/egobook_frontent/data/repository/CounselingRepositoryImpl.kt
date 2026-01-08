package com.example.egobook_frontent.data.repository

import com.example.egobook_frontent.data.api.CounselingApiService
import com.example.egobook_frontent.data.model.toDomain
import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.model.WeeklyReport
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(private val apiService: CounselingApiService): CounselingRepository {
    override suspend fun getDailyPraise(): Result<List<PraiseMessage>> = try {
        val response = apiService.fetchDailyPraise()
        if(response.isSuccessful && response.body() != null) {
            val domainList = response.body()!!.map { it.toDomain() }
            Result.success(domainList)
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWeeklyReport(): Result<List<WeeklyReport>> = try {
        val response = apiService.fetchWeeklyReports()
        if(response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.map { it.toDomain() })
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}