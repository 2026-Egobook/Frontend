package com.example.egobook_frontent.data.repository

import com.example.egobook_frontent.data.api.CounselingService
import com.example.egobook_frontent.data.model.counseling.toDomain
import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.model.ReportStyle
import com.example.egobook_frontent.domain.model.WeeklyReport
import com.example.egobook_frontent.domain.model.WeeklyReportContent
import com.example.egobook_frontent.domain.model.WeeklyReportStyle
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class CounselingRepositoryImpl @Inject constructor(private val apiService: CounselingService): CounselingRepository {
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
        val longDummyText = "이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리 이번 주 분석에 대한 글이 들어가는 자리"

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
        val response = apiService.fetchWeeklyReportStyle()
        if(response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }


    override suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle> = try {
        val response = apiService.updateWeeklyReportStyle(reportStyle = reportStyle)
        if(response.isSuccessful) {
            Result.success(reportStyle)
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}