package com.egobook.app.data.repository

import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.domain.model.DailyData
import com.egobook.app.domain.model.EmotionType
import com.egobook.app.domain.model.MonthData
import com.egobook.app.domain.model.PraiseMessage
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.TimeData
import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.model.WeeklyReportContent
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(private val apiService: CounselingApiService) :
    CounselingRepository {
    override suspend fun getDailyPraise(): Result<List<PraiseMessage>> = try {
//        val response = apiService.fetchDailyPraise()
//        if(response.isSuccessful && response.body() != null) {
//            val domainList = response.body()!!.map { it.toDomain() }
//            Result.success(domainList)
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        val dummyData = listOf(
            PraiseMessage(
                1,
                "어제보다 오늘 더 성장한 당신을 정말 칭찬해요! 칭찬서의 내용이 적히는 자리입니다. 이 영역은 긴 문장이 들어왔을 때 UI가 어떻게 반응하는지 확인하기 위해 작성되었습니다. 당신의 성장은 눈에 보이지 않아도 분명히 진행되고 있어요.",
                "2025.12.30"
            ),
            PraiseMessage(
                2,
                "꾸준히 노력하는 모습이 정말 아름답습니다. 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리",
                "2025.12.31"
            ),
            PraiseMessage(
                3,
                "작은 일에도 최선을 다하는 당신이 자랑스러워요. 때로는 쉬어가는 것도 용기라는 것을 잊지 마세요. 칭찬서의 내용이 적히는 자리입니다. 충분히 잘하고 있고, 앞으로도 당신의 걸음을 응원하겠습니다.",
                "2026.01.01"
            ),
            PraiseMessage(
                4,
                "실패를 두려워하지 않는 용기가 멋집니다. 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리",
                "2026.01.02"
            ),
            PraiseMessage(
                5,
                "오늘 하루도 정말 고생 많으셨습니다. 푹 쉬세요! 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리 칭찬서의 내용이 적히는 자리",
                "2026.01.03"
            )
        )
        Result.success(dummyData)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWeeklyReport(): Result<List<WeeklyReport>> = try {
//        val response = apiService.fetchWeeklyReports()
//        if(response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.map { it.toDomain() })
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        val longDummyText =
            "이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리"

        val dummyWeeklyReports = listOf(
            WeeklyReport(
                id = 1L,
                date = "2025.12.22",
                content = WeeklyReportContent(
                    analysis = "이번 주 분석: $longDummyText",
                    praisePoint = "칭찬 포인트: $longDummyText",
                    improvement = "개선할 점: $longDummyText",
                    management = "관리 및 조언: $longDummyText",
                    encouragement = "응원 및 격려: $longDummyText"
                )
            ),
            WeeklyReport(
                id = 2L,
                date = "2025.12.29",
                content = WeeklyReportContent(
                    analysis = "지난 주 분석: $longDummyText",
                    praisePoint = "지난 주 칭찬: $longDummyText",
                    improvement = "지난 주 개선: $longDummyText",
                    management = "지난 주 조언: $longDummyText",
                    encouragement = "지난 주 격려: $longDummyText"
                )
            )
        )

        Result.success(dummyWeeklyReports)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle> = try {
//        val response = apiService.fetchWeeklyReportStyle()
//        if(response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        Result.success(WeeklyReportStyle(type = ReportStyle.SOFT))
    } catch (e: Exception) {
        Result.failure(e)
    }


    override suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle> =
        try {
//        val response = apiService.updateWeeklyReportStyle(reportStyle = reportStyle)
//        if(response.isSuccessful) {
//            Result.success(reportStyle)
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
            Result.success(reportStyle)
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