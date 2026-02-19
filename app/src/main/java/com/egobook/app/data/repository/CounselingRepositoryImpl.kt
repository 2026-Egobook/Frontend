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
import com.egobook.app.domain.model.counseling.CounselingReward
import com.egobook.app.domain.model.counseling.CounselingRewardType
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
//        val response = apiService.fetchDailyPraiseByDate(date = date)
//        if (response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        val mockDetails = listOf(
            DailyPraiseDetail(
                diaryDate = "2024-02-03",
                content = """
                새로운 한 달의 시작을 차분하게 잘 열어가고 계시네요.
                당신이 가진 긍정적인 태도는 주변 사람들에게도 좋은 영향을 줍니다.
                오늘 마주한 작은 행복들을 소중히 간직하며 하루를 마무리하세요.
                내일도 당신의 앞날에 따뜻한 햇살이 가득하기를 진심으로 바랍니다.
                충분한 휴식과 함께 편안한 밤 되시길 응원하겠습니다.
            """.trimIndent(),
                createdAt = "2024-02-03T19:15:30",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-05",
                content = """
                월요일의 무게를 견디고 묵묵히 자신의 자리를 지킨 당신이 대견합니다.
                가끔은 지치기도 하겠지만, 당신은 생각보다 훨씬 더 강한 사람이에요.
                스스로에게 너무 엄격하기보다 '오늘도 잘했다'고 한마디 건네주세요.
                당신의 꾸준함이 모여 결국은 커다란 결실을 맺게 될 것임을 믿습니다.
                포근한 이불 속에서 오늘 하루의 긴장을 모두 녹여내시길 바라요.
            """.trimIndent(),
                createdAt = "2024-02-05T20:40:12",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-06",
                content = """
                어제보다 조금 더 성장한 오늘의 당신을 진심으로 칭찬합니다.
                사소해 보이는 일상 속에서도 당신만의 의미를 찾아내는 모습이 참 아름다워요.
                타인의 속도에 조급해하지 말고 지금처럼 당신만의 길을 걸어가 주세요.
                세상은 당신의 진심 어린 노력을 반드시 기억하고 보상해 줄 것입니다.
                오늘 밤은 걱정 없이 깊고 단잠을 자며 에너지를 충전하세요.
            """.trimIndent(),
                createdAt = "2024-02-06T18:55:45",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-09",
                content = """
                한 주의 마무리를 향해 달려가는 당신의 열정에 박수를 보냅니다.
                힘든 순간에도 미소를 잃지 않으려 노력하는 모습이 정말 인상적이에요.
                당신은 존재만으로도 충분히 가치 있고 빛나는 보석 같은 사람입니다.
                오늘 하루 고생한 자신을 위해 맛있는 음식이나 작은 선물을 주면 어떨까요?
                당신의 내일이 오늘보다 더 평온하고 행복하기를 항상 기도할게요.
            """.trimIndent(),
                createdAt = "2024-02-09T21:10:05",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-11",
                content = """
                오늘 하루 정말 고생 많으셨어요. 
                비록 작은 실수들이 있었을지라도 그것은 성장을 위한 과정일 뿐이에요.
                당신이 보여준 인내와 노력은 결코 헛되지 않았으며, 
                내일은 오늘보다 조금 더 밝은 미소를 지을 수 있을 거예요.
                스스로를 조금 더 믿고 편안하게 휴식을 취하시길 바랍니다.
            """.trimIndent(),
                createdAt = "2024-02-11T18:30:15",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-13",
                content = """
                누구보다 성실하게 오늘을 살아낸 당신이 자랑스럽습니다.
                복잡한 생각들은 잠시 내려놓고 마음의 소리에 귀를 기울여 보세요.
                당신은 충분히 사랑받을 자격이 있는 소중한 사람이며, 
                주변 사람들에게 긍정적인 에너지를 주는 특별한 존재입니다.
                오늘 밤은 당신의 노력을 칭찬하며 깊은 잠에 드시길 응원해요.
            """.trimIndent(),
                createdAt = "2024-02-13T19:45:22",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-14",
                content = """
                어려운 상황 속에서도 포기하지 않고 묵묵히 나아가는 모습이 멋져요.
                타인의 기준에 맞추려 애쓰기보다 당신만의 속도를 존중해 주세요.
                가끔은 쉬어가는 것도 더 멀리 나아가기 위한 소중한 전략입니다.
                오늘 하루 당신이 뿌린 작은 씨앗들이 곧 예쁜 꽃을 피울 거예요.
                당신의 모든 걸음을 진심으로 지지하고 응원하고 있습니다.
            """.trimIndent(),
                createdAt = "2024-02-14T20:10:05",
                isRead = true,
                rewards = emptyList()
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-16",
                content = """
                오늘의 당신은 어제보다 한 뼘 더 성장한 멋진 사람입니다.
                사소한 성취 하나에도 자신을 마음껏 칭찬해 주는 건 어떨까요?
                당신이 가진 따뜻한 마음씨는 세상을 조금 더 밝게 만듭니다.
                남들의 시선보다는 당신의 행복을 최우선으로 생각했으면 좋겠어요.
                수고한 당신에게 따뜻한 차 한 잔과 같은 평온함이 깃들길 바랍니다.
            """.trimIndent(),
                createdAt = "2024-02-16T17:55:40",
                isRead = false,
                rewards = listOf(
                    CounselingReward(
                        kind = CounselingRewardType.SELF_ESTEEM,
                        amount = 1,
                        toastMessage = "칭찬서가 확인하여\n자존감이 상승했어요"
                    )
                )
            ),
            DailyPraiseDetail(
                diaryDate = "2024-02-18",
                content = """
                마음이 무거운 날이었을지도 모르지만, 당신은 충분히 잘 해냈습니다.
                내일의 걱정은 내일에게 맡기고 지금 이 순간의 평안을 누려보세요.
                당신의 존재 자체만으로도 누군가에게는 큰 힘과 위로가 됩니다.
                스스로를 다독여주는 시간을 가지며 오늘 하루를 마무리해 보세요.
                당신의 빛나는 미래를 믿어 의심치 않으며 늘 곁에서 응원할게요.
            """.trimIndent(),
                createdAt = "2024-02-18T21:20:12",
                isRead = false,
                rewards = listOf(
                    CounselingReward(
                        kind = CounselingRewardType.SELF_ESTEEM,
                        amount = 1,
                        toastMessage = "칭찬서가 확인하여\n자존감이 상승했어요"
                    )
                )
            )
        )

        // 2. 인자로 들어온 date와 일치하는 데이터 찾기 (없으면 첫 번째 데이터 반환)
        val result = mockDetails.find { it.diaryDate == date } ?: mockDetails.first()
        Result.success(result)
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
//            val response = apiService.fetchWeeklyReportByDate(startDate = startDate)
//            if (response.isSuccessful && response.body() != null) {
//                Result.success(response.body()!!.toDomain())
//            } else {
//                Result.failure(Exception("Error: ${response.code()}"))
//            }
            val dummyDetail = listOf(
                WeeklyReportDetail(
                    startDate = "2026-02-02",
                    endDate = "2026-02-08",
                    summary = """
                    이번 주 상담의 핵심 주제는 직무 몰입도 저하와 그로 인한 심리적 소진이었습니다.
                    사용자는 업무 성과에 대한 과도한 압박감으로 인해 일상적인 즐거움을 잃어버린 상태였으며,
                    상담을 통해 번아웃의 초기 증상을 객관적으로 점검하고 휴식의 필요성을 인지했습니다.
                    자신을 업무 성과와 동일시하는 인지적 오류를 발견하고 이를 수정하는 연습을 진행했습니다.
                    현재는 업무 시간 외에 철저히 분리된 자신만의 시간을 확보하는 것에 집중하고 있습니다.
                    감정적인 소모가 심했던 한 주였지만, 문제의 원인을 명확히 규명했다는 점에서 의미가 큽니다.
                    다음 단계로는 완벽주의적 성향을 완화하고 자기 연민을 실천하는 과정을 계획 중입니다.
                """.trimIndent(),
                    praisePoints = """
                    자신의 한계를 인정하고 상담을 통해 도움을 요청한 용기 있는 태도를 높게 평가합니다.
                    업무 압박 속에서도 하루 10분간의 정기적인 스트레칭을 실천하며 신체 감각을 깨웠습니다.
                    부정적인 생각이 꼬리에 꼬리를 물 때 '멈춤' 신호를 스스로에게 보낼 수 있게 되었습니다.
                    동료와의 갈등 상황에서 감정적으로 대응하지 않고 차분하게 자신의 의사를 전달했습니다.
                    자신의 취약점을 숨기려 하기보다 솔직하게 대면하며 변화를 갈망하는 모습이 인상적입니다.
                    과거에 비해 자신의 정서적 상태를 언어로 표현하는 능력이 눈에 띄게 정교해졌습니다.
                    어려운 환경 속에서도 매일 아침 감사한 점 한 가지를 찾아낸 끈기를 칭찬하고 싶습니다.
                """.trimIndent(),
                    improvementPoints = """
                    여전히 퇴근 후에도 업무 관련 연락을 확인하며 온전한 휴식을 방해하는 습관이 남아있습니다.
                    스스로에게 부여하는 높은 기준이 때로는 독이 되어 자존감을 깎아내리고 있는 점이 우려됩니다.
                    작은 실수에도 과도하게 자책하며 전체적인 성과를 부정하는 이분법적 사고를 경계해야 합니다.
                    신체적인 피로가 누적되어 있음에도 불구하고 운동을 강박적으로 수행하려는 경향이 보입니다.
                    자신의 감정을 억누르는 것이 익숙해져 정작 슬픔이나 화가 날 때 적절히 분출하지 못합니다.
                    주변의 기대에 부응하려는 욕구 때문에 본인의 진정한 욕구를 뒷전으로 미루는 점이 보입니다.
                    충분한 수면 시간을 확보하지 못해 정서적 회복 탄력성이 다소 낮아진 상태로 판단됩니다.
                """.trimIndent(),
                    managementAdvice = """
                    이번 주는 디지털 디톡스를 통해 뇌에 충분한 휴식을 제공하는 시간을 반드시 가지세요.
                    퇴근 후에는 업무용 메신저 알림을 끄고 물리적으로 스마트폰과 거리를 두는 연습이 필요합니다.
                    점심시간을 활용해 15분간 야외에서 햇볕을 쬐며 걷는 활동은 세로토닌 분비에 큰 도움이 됩니다.
                    자신을 비난하는 목소리가 들릴 때마다 '그럴 수 있어'라는 문장을 입 밖으로 소리 내어 말해보세요.
                    복잡한 생각보다는 단순한 수작업이나 취미 활동을 통해 몰입의 즐거움을 다시 느껴보시기 바랍니다.
                    주말 중 하루는 계획 없이 흐르는 대로 시간을 보내며 생산성에 대한 강박을 내려놓으세요.
                    일기 작성 시 결과 중심이 아닌, 그날 느꼈던 사소한 기분들에 집중해 기록해 보길 권장합니다.
                """.trimIndent(),
                    supportMessage = """
                    당신은 이미 충분히 많은 짐을 지고 달려왔으며, 이제는 잠시 짐을 내려놓아도 괜찮습니다.
                    멈추는 것은 퇴보가 아니라 더 멀리 나아가기 위한 가장 현명한 전략이자 자기 사랑의 실천입니다.
                    세상의 기준이 아닌 당신만의 속도로 걸어가는 모습 그 자체로 당신은 충분히 가치 있는 사람입니다.
                    비바람이 치는 날이 있으면 해가 뜨는 날도 있듯이, 지금의 힘든 감정도 결국 지나갈 것입니다.
                    스스로를 다독이는 법을 배워가는 당신의 뒷모습을 진심으로 응원하고 곁에서 지켜보겠습니다.
                    내일은 오늘보다 조금 더 가벼운 마음으로 아침을 맞이할 수 있기를 간절히 기도합니다.
                    당신은 혼자가 아니며, 우리는 이 과정을 함께 헤쳐 나갈 준비가 되어 있다는 것을 잊지 마세요.
                """.trimIndent(),
                    isRead = false
                ),
                WeeklyReportDetail(
                    startDate = "2026-02-09",
                    endDate = "2026-02-15",
                    summary = """
                    이번 주 상담은 타인과의 관계에서 발생하는 불안감을 다스리고 자존감을 회복하는 데 주력했습니다.
                    타인의 시선을 지나치게 의식하여 본인의 의사를 표현하지 못했던 상황들을 구체적으로 분석했습니다.
                    상담을 통해 거절이 관계를 망치는 것이 아니라 건강하게 유지하는 수단임을 새롭게 깨달았습니다.
                    일상 속에서 가벼운 '나 전달법(I-Message)'을 활용해 본인의 감정을 드러내는 실습을 병행했습니다.
                    과거의 상처가 현재의 관계에 미치는 영향력을 인지하며 심리적 독립을 위한 초석을 다졌습니다.
                    전반적으로 대인 관계에 대한 공포심이 줄어들고 자신감을 조금씩 회복해가는 긍정적인 추세입니다.
                    스스로를 더 사랑하고 아끼는 구체적인 방법들을 모색하며 한 주를 마무리했습니다.
                """.trimIndent(),
                    praisePoints = """
                    상대방의 불편한 부탁에 대해 처음으로 정중하게 거절의 의사를 표시한 점은 매우 놀라운 발전입니다.
                    자신의 감정을 숨기지 않고 상담사에게 솔직하게 털어놓으며 깊은 라포를 형성하려 노력했습니다.
                    주변의 비판적인 시선에 대해 이전보다 덜 민감하게 반응하며 평정심을 유지하는 모습을 보였습니다.
                    매일 거울을 보며 자신에게 긍정적인 확언을 해주는 루틴을 성실히 이행한 점이 훌륭합니다.
                    갈등 상황에서도 회피하지 않고 끝까지 대화를 이어가려 노력한 끈기가 돋보였습니다.
                    자신이 가진 장점 10가지를 직접 적어보며 스스로의 가치를 재발견하려는 시도가 좋았습니다.
                    타인에게 베푸는 친절만큼이나 자신에게도 친절하려 노력하는 태도의 변화가 매우 감동적입니다.
                """.trimIndent(),
                    improvementPoints = """
                    아직은 대화 중 정적이 흐를 때 과도한 불안감을 느끼며 대화를 주도해야 한다는 강박이 있습니다.
                    타인의 사소한 표정 변화를 본인의 잘못으로 연결 지어 생각하는 관계 사고가 관찰됩니다.
                    자신의 의견을 말한 뒤 상대방의 눈치를 보며 금방 말을 수정하거나 사과하는 습관이 남아있습니다.
                    칭찬을 받았을 때 이를 온전히 받아들이지 못하고 부정하거나 겸손을 떨며 깎아내리곤 합니다.
                    혼자 있는 시간을 외로움으로만 치부하며 이를 견디기 힘들어하는 경향이 여전히 존재합니다.
                    상대방의 기분을 맞춰주기 위해 자신의 스케줄을 무리하게 조정하는 모습이 가끔 보입니다.
                    과거에 겪었던 인간관계의 실패 사례를 현재의 새로운 관계에 대입하여 미리 걱정하는 점이 있습니다.
                """.trimIndent(),
                    managementAdvice = """
                    이번 주에는 혼자만의 시간을 '고립'이 아닌 '충전'의 시간으로 정의하고 즐겨보시기 바랍니다.
                    카페에서 혼자 책을 읽거나 좋아하는 음악을 들으며 오롯이 자신에게 집중하는 경험을 늘리세요.
                    누군가와 대화할 때 상대방의 반응에 집중하기보다 본인이 전하고 싶은 메시지에 더 집중해 보세요.
                    본인의 감정을 기록할 때 '기쁘다', '슬프다' 외에 더 세밀한 감정 단어들을 사용해 보길 권합니다.
                    매일 밤 잠들기 전 오늘 하루 고생한 자신에게 "오늘도 고마웠어"라고 진심으로 말해 주십시오.
                    불필요한 인맥을 정리하고 진정으로 본인을 지지해 주는 사람들과의 시간에 에너지를 쓰세요.
                    예기치 못한 상황이 발생해도 '그럴 수도 있지, 괜찮아'라고 넘기는 대범함을 연습해 보세요.
                """.trimIndent(),
                    supportMessage = """
                    당신은 존재 자체로 소중하며, 타인의 인정이 없어도 당신의 빛은 사라지지 않습니다.
                    타인의 기대를 만족시키는 삶이 아닌, 당신의 마음이 시키는 대로 살아갈 용기를 응원합니다.
                    지금 겪고 있는 관계의 진통은 당신이 더 단단한 나무로 성장하기 위한 과정일 뿐입니다.
                    조금 서툴러도 괜찮고, 때로는 실수해도 괜찮으니 당신 자신을 가장 먼저 아껴주길 바랍니다.
                    당신은 사랑받을 자격이 충분한 사람이며, 그 사실은 어떤 순간에도 변하지 않는 진실입니다.
                    함께 걷는 이 길 위에서 당신이 더 환하게 웃을 수 있는 날이 올 것임을 확신합니다.
                    당신의 내일이 오늘보다 더 평온하고, 당신의 마음속에 따뜻한 온기가 가득하기를 응원합니다.
                """.trimIndent(),
                    isRead = true
                )
            )
            val targetDetail = dummyDetail.single { it.startDate == startDate }
            Result.success(targetDetail)
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