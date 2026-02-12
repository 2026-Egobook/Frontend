package com.egobook.app.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.CounselingNotificationRequest
import com.egobook.app.data.model.counseling.ReportStyleRequest
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.data.repository.paging.DailyPraisePagingSource
import com.egobook.app.data.repository.paging.WeeklyReportsPagingSource
import com.egobook.app.domain.model.DailyData
import com.egobook.app.domain.model.EmotionType
import com.egobook.app.domain.model.MonthData
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.TimeData
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.egobook.app.domain.model.counseling.DailyPraise
import com.egobook.app.domain.model.counseling.DailyPraiseDetail
import com.egobook.app.domain.model.counseling.WeeklyReport
import com.egobook.app.domain.model.counseling.WeeklyReportDetail
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.domain.repository.CounselingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(private val apiService: CounselingApiService) :
    CounselingRepository {
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
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.toDomain())
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

    override suspend fun getWeeklyReportByDate(startDate: String): Result<WeeklyReportDetail> =
        try {
            val response = apiService.fetchWeeklyReportByDate(startDate = startDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
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


    override suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle> =
        try {
            val response =
                apiService.updateWeeklyReportStyle(request = ReportStyleRequest(toneStyle = reportStyle))
            if (response.isSuccessful) {
                Result.success(reportStyle)
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
//        val response = apiService.fetchStatistics()
//        if (response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        fun createEmptyTime() = TimeData(List(24) { 0 })
        fun createEmptyDay() = DailyData(List(7) { createEmptyTime() })
        fun createEmptyMonth() = MonthData(List(12) { createEmptyDay() })

        /**
         * 1. 기본 구조 생성 (모든 감정 초기화)
         */
        val emotionMap = EmotionType.entries.associateWith { createEmptyMonth() }.toMutableMap()

        // --- 데이터 주입 시작 ---

        /**
         * 2. '매우 기쁨' (VERY_GOOD)
         * 패턴: 금, 토 오후 피크
         */
        val veryGoodMonths = List(12) { mIdx ->
            DailyData(List(7) { dIdx ->
                TimeData(List(24) { hIdx ->
                    if ((dIdx == 4 || dIdx == 5) && hIdx in 17..19) 15 else 0
                })
            })
        }
        emotionMap[EmotionType.VERY_GOOD] = MonthData(veryGoodMonths)

        /**
         * 3. '기쁨' (GOOD) - 추가됨
         * 패턴: 평일(월~금) 퇴근 시간대(18~20시)에 꾸준히 발생
         */
        val goodMonths = List(12) { mIdx ->
            DailyData(List(7) { dIdx ->
                TimeData(List(24) { hIdx ->
                    if (dIdx in 0..4 && hIdx in 18..20) 8 else 0
                })
            })
        }
        emotionMap[EmotionType.GOOD] = MonthData(goodMonths)

        /**
         * 4. '보통' (NORMAL)
         * 패턴: 수, 목 낮 시간(10~16시) 분포
         */
        val normalMonths = List(12) { mIdx ->
            DailyData(List(7) { dIdx ->
                TimeData(List(24) { hIdx ->
                    if (dIdx in 2..3 && hIdx in 10..16) 5 else 0
                })
            })
        }
        emotionMap[EmotionType.NORMAL] = MonthData(normalMonths)

        /**
         * 5. '슬픔' (BAD) - 추가됨
         * 패턴: 화, 목 늦은 밤(23시~01시) 감수성이 풍부해지는 시간
         */
        val badMonths = List(12) { mIdx ->
            DailyData(List(7) { dIdx ->
                TimeData(List(24) { hIdx ->
                    if ((dIdx == 1 || dIdx == 3) && (hIdx >= 23 || hIdx <= 1)) 6 else 0
                })
            })
        }
        emotionMap[EmotionType.BAD] = MonthData(badMonths)

        /**
         * 6. '매우 슬픔' (VERY_BAD)
         * 패턴: 월요일 아침 피크 & 일요일 밤 월요병
         */
        val veryBadMonths = List(12) { mIdx ->
            DailyData(List(7) { dIdx ->
                TimeData(List(24) { hIdx ->
                    when {
                        dIdx == 0 && hIdx in 8..9 -> 12
                        dIdx == 6 && hIdx == 22 -> 7
                        else -> 0
                    }
                })
            })
        }
        emotionMap[EmotionType.VERY_BAD] = MonthData(veryBadMonths)

        /**
         * 7. 월별 트렌드 차트 (Line Chart) 보정
         * 11월(현재)은 기쁘게, 10월(지난달)은 슬프게 세팅하여 상승 곡선 유도
         */
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val lastMonth = (currentMonth + 11) % 12

        // 이번 달: VERY_GOOD 데이터 대폭 추가
        val currentMonthHappy = DailyData(List(7) { TimeData(List(24) { 20 }) })
        val newVeryGoodList = emotionMap[EmotionType.VERY_GOOD]!!.months.toMutableList()
        newVeryGoodList[currentMonth] = currentMonthHappy
        emotionMap[EmotionType.VERY_GOOD] = MonthData(newVeryGoodList)

        // 지난 달: VERY_BAD 데이터 대폭 추가
        val lastMonthSad = DailyData(List(7) { TimeData(List(24) { 15 }) })
        val newVeryBadList = emotionMap[EmotionType.VERY_BAD]!!.months.toMutableList()
        newVeryBadList[lastMonth] = lastMonthSad
        emotionMap[EmotionType.VERY_BAD] = MonthData(newVeryBadList)

        // 최종 결과 반환
        Result.success(Statistics(emotions = emotionMap))
    } catch (e: Exception) {
        Result.failure(e)
    }
}