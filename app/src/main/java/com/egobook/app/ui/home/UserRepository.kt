package com.egobook.app.ui.home

import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun load(): User
}

interface NetworkUserService {
    @GET("/home")
    suspend fun loadResponse(): BaseResponse<UserDto>
}

data class BaseResponse<T>(
    val code: String,
    val message: String,
    val status: Int,
    val data: T
)

data class UserDto(
    val nickname: String,
    val level: Int,
    val ink: Int,
    val unreadNotifications: Int,
    val hasUnopenedPsychology: Boolean,
    val isFirstAttendanceToday: Boolean,
    val attendanceRewardInk: Int
) {
    fun toDomain(): User = User(Level(level), Ink(ink))
}

@Singleton
class NetworkUserRepository @Inject constructor(
    private val retrofit: Retrofit
) : UserRepository {
    private val userService by lazy { retrofit.create(NetworkUserService::class.java) }
    override suspend fun load(): User {
        val userServiceResponse: BaseResponse<UserDto> = userService.loadResponse()
        return userServiceResponse.data.toDomain()
    }
}
