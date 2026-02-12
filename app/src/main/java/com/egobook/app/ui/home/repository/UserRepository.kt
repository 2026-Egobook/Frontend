package com.egobook.app.ui.home.repository

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

interface UserAdRepository {
    suspend fun loadAdInfo(): AdInfoDto
}

interface UserTendencyRepository {
    suspend fun loadTendencies(): List<Tendency>
}

interface NetworkUserService {
    @GET("/home")
    suspend fun loadBasicUserInformation(): BaseResponse<UserDto>
}

interface NetworkTendencyLevelService {
    @GET("/home/abilities")
    suspend fun loadTendencyLevels(): BaseResponse<TendencyLevels>
}

interface NetworkAdService {
    @GET("/ads/info")
    suspend fun loadAdInfo(): BaseResponse<AdInfoDto>
}

data class AdInfoDto(
    val currentViewCount: Int,
    val maxLimit: Int,
    val isAvailable: Boolean,
    val rewardPerAd: Int,
    val message: String
)

@Singleton
class NetworkUserRepository @Inject constructor(
    @BackendApi private val retrofit: Retrofit
) : UserRepository, UserTendencyRepository, UserActivityRepository, UserAdRepository {
    private val userService by lazy { retrofit.create(NetworkUserService::class.java) }
    private val tendencyLevelService by lazy { retrofit.create(NetworkTendencyLevelService::class.java) }
    private val activityRecordService by lazy { retrofit.create(NetworkActivityRecordService::class.java) }

    private val networkAdService by lazy { retrofit.create(NetworkAdService::class.java) }


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

    override suspend fun loadAdInfo(): AdInfoDto {
        val adInfoResponse: BaseResponse<AdInfoDto> = networkAdService.loadAdInfo()
        return adInfoResponse.data
    }
}
