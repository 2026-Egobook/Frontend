package com.egobook.app.domain.usecase

import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.home.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = try {
        val user = userRepository.load()
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }
}