package com.egobook.app.ui.home.repository

import com.egobook.app.ui.home.user.User
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
