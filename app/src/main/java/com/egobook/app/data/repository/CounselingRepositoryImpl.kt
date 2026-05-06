package com.egobook.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.CounselingNotificationRequest
import com.egobook.app.data.model.counseling.ReportStyleRequest
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.data.repository.paging.DailyPraisePagingSource
import com.egobook.app.data.repository.paging.WeeklyReportsPagingSource
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.egobook.app.domain.model.counseling.DailyPraise
import com.egobook.app.domain.model.counseling.DailyPraiseDetail
import com.egobook.app.domain.model.counseling.WeeklyReport
import com.egobook.app.domain.model.counseling.WeeklyReportDetail
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.domain.repository.CounselingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(
    private val apiService: CounselingApiService
) : CounselingRepository {

    override fun getDailyPraise(size: Int): Flow<PagingData<DailyPraise>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DailyPraisePagingSource(apiService = apiService)
            }
        ).flow
    }

    override suspend fun getDailyPraiseByDate(date: String): Result<DailyPraiseDetail> = try {
        val response = apiService.fetchDailyPraiseByDate(date = date)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDailyAndWeeklyNotification(): Result<DailyAndWeeklyNotification> = try {
        val response = apiService.fetchDailyAndWeeklyNotification()
        if (response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateDailyPraiseNotification(isEnabled: Boolean): Result<Boolean> = try {
        val response = apiService.updateDailyPraiseNotification(
            request = CounselingNotificationRequest(isEnabled = isEnabled)
        )
        if (response.status == 200) {
            Result.success(isEnabled)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateWeeklyReportNotification(isEnabled: Boolean): Result<Boolean> = try {
        val response = apiService.updateWeeklyReportNotification(
            request = CounselingNotificationRequest(isEnabled = isEnabled)
        )
        if (response.status == 200) {
            Result.success(isEnabled)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getWeeklyReports(size: Int): Flow<PagingData<WeeklyReport>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WeeklyReportsPagingSource(apiService = apiService)
            }
        ).flow
    }

    override suspend fun getWeeklyReportByDate(startDate: String): Result<WeeklyReportDetail> = try {
        val response = apiService.fetchWeeklyReportByDate(startDate = startDate)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWeeklyReportStyle(): Result<ReportStyle> = try {
        val response = apiService.fetchWeeklyReportStyle()
        if (response.status == 200) {
            Result.success(response.data)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle> = try {
        val response = apiService.updateWeeklyReportStyle(
            request = ReportStyleRequest(toneStyle = reportStyle)
        )
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body.tone)
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unlockWeeklyReport(
        startDate: String,
        unlockType: WeeklyReportUnlockType
    ): Result<Unit> = try {
        val response = apiService.unlockWeeklyReport(startDate = startDate, unlockType = unlockType)
        if (response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getStatistics(): Result<Statistics> = try {
        val response = apiService.fetchStatistics()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
