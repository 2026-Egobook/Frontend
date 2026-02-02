package com.egobook.app.ui.home

import com.egobook.app.data.interceptor.AuthInterceptor
import com.egobook.app.di.NetworkModule
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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

class NetworkUserRepository : UserRepository {
    override suspend fun load(): User {
        val httpClient = NetworkModule.provideOkHttpClient(AuthInterceptor())
        val retrofit = NetworkModule.provideRetrofit(httpClient, GsonConverterFactory.create())
        val userService = retrofit.create(NetworkUserService::class.java)

        val userServiceResponse: BaseResponse<UserDto> = userService.loadResponse()
        return userServiceResponse.data.toDomain()
    }
}