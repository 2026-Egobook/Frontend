package com.egobook.app.ui.home.repository

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

interface NetworkUserService {
    @GET("/home")
    suspend fun loadBasicUserInformation(): BaseResponse<UserDto>
}

interface NetworkTendencyLevelService {
    @GET("/home/abilities")
    suspend fun loadTendencyLevels(): BaseResponse<TendencyLevels>
}

@Singleton
class NetworkUserRepository @Inject constructor(
    private val retrofit: Retrofit
) : UserRepository, UserTendencyRepository {
    private val userService by lazy { retrofit.create(NetworkUserService::class.java) }
    private val tendencyLevelService by lazy { retrofit.create(NetworkTendencyLevelService::class.java) }
    override suspend fun load(): User {
        val userServiceResponse: BaseResponse<UserDto> = userService.loadBasicUserInformation()
        return userServiceResponse.data.toDomain()
    }

    override suspend fun loadTendencies(): List<Tendency> {
        val tendencyLevelResponse: BaseResponse<TendencyLevels> = tendencyLevelService.loadTendencyLevels()
        return tendencyLevelResponse.data.toDomain()
    }
}
