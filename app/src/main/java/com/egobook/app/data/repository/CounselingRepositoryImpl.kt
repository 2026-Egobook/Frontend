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
//        return Pager(
//            config = PagingConfig(
//                pageSize = size,
//                initialLoadSize = size,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = {
//                WeeklyReportsPagingSource(apiService = apiService)
//            }
//        ).flow

        val dummyWeeklyReports = listOf(
            WeeklyReport(
                id = 1L,
                startDate = "2024.02.03",
                endDate = "2024.02.09",
                isRead = true,
                isLocked = false
            ),
            WeeklyReport(
                id = 2L,
                startDate = "2024.01.27",
                endDate = "2024.02.02",
                isRead = false,
                isLocked = false
            ),
            WeeklyReport(
                id = 3L,
                startDate = "2024.01.20",
                endDate = "2024.01.26",
                isRead = false,
                isLocked = true // 잠긴 상태 테스트
            ),
            WeeklyReport(
                id = 4L,
                startDate = "2024.01.13",
                endDate = "2024.01.19",
                isRead = false,
                isLocked = true
            )
        )

        // 2. Pager 대신 flowOf와 PagingData.from을 사용하여 즉시 반환
        return flowOf(
            PagingData.from(dummyWeeklyReports)
        )
    }

    override suspend fun getWeeklyReportByDate(startDate: String): Result<WeeklyReportDetail> =
        try {
//            val response = apiService.fetchWeeklyReportByDate(startDate = startDate)
//            if (response.isSuccessful && response.body() != null) {
//                Result.success(response.body()!!.toDomain())
//            } else {
//                Result.failure(Exception("Error: ${response.code()}"))
//            }
            val dummyDetail = WeeklyReportDetail(
                startDate = startDate, // 클릭한 아이템의 날짜를 그대로 사용
                endDate = "2024.02.09",
                summary = """
                이번 한 주 동안 사용자님은 정말 꾸준한 감정 기록을 보여주셨습니다. 
                특히 주 초반에 겪었던 업무적 스트레스를 수요일 이후 스스로 잘 극복해내는 과정이 인상 깊었습니다. 
                금요일에는 예상치 못한 즐거운 소식으로 인해 긍정적인 에너지가 최고조에 달했으며, 
                이러한 긍정적인 흐름이 주말까지 이어져 안정적인 심리 상태를 유지하셨습니다. 
                전반적으로 감정의 기복이 있었으나 스스로를 잘 돌보신 한 주였습니다.
            """.trimIndent(),
                praisePoints = """
                가장 칭찬하고 싶은 점은 스트레스 상황에서도 기록을 포기하지 않은 끈기입니다. 
                자신의 감정을 외면하지 않고 있는 그대로 마주하려는 태도가 매우 훌륭합니다. 
                부정적인 감정이 들 때마다 짧은 명상을 시도한 점이 심리적 회복 탄력성을 높였습니다. 
                또한 주변 사람들과 긍정적인 대화를 나누며 에너지를 얻으려 노력한 모습도 보기 좋습니다. 
                작은 성취들을 기록하며 자신감을 되찾으려는 모습이 사용자님의 가장 큰 강점입니다.
            """.trimIndent(),
                improvementPoints = """
                화요일 밤에 나타난 수면 부족 현상이 수요일 오전 집중력 저하로 이어진 것으로 보입니다. 
                감정이 격해질 때 가끔 식사를 거르는 습관이 체력적인 부담을 줄 수 있으니 주의가 필요합니다. 
                타인의 시선을 과도하게 신경 써서 본인의 진심을 숨기려는 경향이 간혹 관찰됩니다. 
                불안함이 느껴질 때 너무 빠르게 결론을 내리려 하기보다는 시간을 두고 지켜보는 여유가 필요합니다. 
                일과 삶의 경계가 모호해지지 않도록 명확한 휴식 시간을 설정하는 것을 추천드립니다.
            """.trimIndent(),
                managementAdvice = """
                다음 주에는 하루에 10분이라도 온전히 스마트폰을 멀리하고 혼자만의 시간을 가져보세요. 
                특히 감정이 복잡할 때는 글로 직접 써 내려가는 '감정 쓰기'가 큰 도움이 될 것입니다. 
                점심시간을 활용한 가벼운 산책이 오후 시간의 집중력과 기분 전환에 효과적일 것입니다. 
                불안한 생각이 들 때는 호흡에 집중하며 현재의 감각을 느껴보는 연습을 지속해 보세요. 
                스스로에게 너무 엄격한 잣대를 대기보다는 '그럴 수도 있지'라는 마음가짐이 필요합니다.
            """.trimIndent(),
                supportMessage = """
                사용자님, 이번 주도 정말 고생 많으셨습니다. 당신은 이미 충분히 잘하고 있습니다. 
                때로는 비가 오기도 하지만, 그 비가 땅을 단단하게 만들어 더 아름다운 꽃을 피우게 할 거예요. 
                작은 감정의 변화에 일희일비하기보다, 꾸준히 나아가고 있는 자신의 발걸음을 믿어보세요. 
                어떤 순간에도 저는 사용자님의 편이 되어 응원하고 기록을 도와드릴 것입니다. 
                따뜻한 차 한 잔과 함께 편안한 저녁 보내시길 바라며, 내일도 힘차게 시작해 봐요!
            """.trimIndent(),
                isRead = true
            )
            Result.success(dummyDetail)
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