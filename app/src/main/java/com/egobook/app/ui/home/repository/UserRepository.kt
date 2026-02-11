package com.egobook.app.ui.home.repository

import android.util.Log
import com.egobook.app.di.qualifier.BackendApi
import com.egobook.app.ui.home.user.Tendency
import com.egobook.app.ui.home.user.User
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun load(): User
}

interface UserTendencyRepository {
    suspend fun loadTendencies(): List<Tendency>
}

interface UserPsychologyRepository {
    suspend fun isReadDailyPsychology(): Boolean
    suspend fun loadDailyPsychology(): DailyPsychologyDto
}

interface NetworkUserService {
    @GET("/home")
    suspend fun loadBasicUserInformation(): BaseResponse<UserDto>
}

interface NetworkTendencyLevelService {
    @GET("/home/abilities")
    suspend fun loadTendencyLevels(): BaseResponse<TendencyLevels>
}

data class PsychologyStateDto(
    val isBottleVisible: Boolean
)

data class PsychologyKnowledge(
    val knowledgeId: Int,
    val title: String,
    val content: String,
    val source: String
)

data class PsychologyReward(
    val granted: Boolean,
    val inkGranted: Int,
    val inkBalance: Int,
    val toastMessage: String
)
data class DailyPsychologyDto(
    val date: String,
    val knowledge: PsychologyKnowledge,
    val reward: PsychologyReward?,
    val isBookmarked: Boolean

)

interface NetworkPsychologyService {
    @GET("/psychology/daily/status")
    suspend fun isReadDailyPsychology(): BaseResponse<PsychologyStateDto>

    @GET("/psychology/daily")
    suspend fun loadDailyPsychology(): BaseResponse<DailyPsychologyDto>
}

@Singleton
class NetworkUserRepository @Inject constructor(
    @BackendApi private val retrofit: Retrofit
) : UserRepository, UserTendencyRepository, UserActivityRepository, UserPsychologyRepository {
    private val userService by lazy { retrofit.create(NetworkUserService::class.java) }
    private val tendencyLevelService by lazy { retrofit.create(NetworkTendencyLevelService::class.java) }
    private val activityRecordService by lazy { retrofit.create(NetworkActivityRecordService::class.java) }

    private val psychologyService by lazy { retrofit.create(NetworkPsychologyService::class.java) }

    override suspend fun load(): User {
        val userServiceResponse: BaseResponse<UserDto> = userService.loadBasicUserInformation()
        return userServiceResponse.data.toDomain()
    }

    override suspend fun loadTendencies(): List<Tendency> {
        val tendencyLevelResponse: BaseResponse<TendencyLevels> =
            tendencyLevelService.loadTendencyLevels()
        return tendencyLevelResponse.data.toDomain()
    }

    override suspend fun loadActivityRecord(): ActivityRecord {
        val activityRecordResponse: BaseResponse<ActivityRecordDto> =
            activityRecordService.loadUserActivityRecord()
        return activityRecordResponse.data.toDomain()
    }

    override suspend fun isReadDailyPsychology(): Boolean {
        val psychologyResponse: BaseResponse<PsychologyStateDto> =
            psychologyService.isReadDailyPsychology()
        Log.d("jang", "isReadDailyPsychology: ${psychologyResponse.data.isBottleVisible}")
        return psychologyResponse.data.isBottleVisible
    }

    override suspend fun loadDailyPsychology(): DailyPsychologyDto {
        val psychologyResponse: BaseResponse<DailyPsychologyDto> =
            psychologyService.loadDailyPsychology()
        return psychologyResponse.data
    }
}
