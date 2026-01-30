package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class RejectFriendRequestUseCase @Inject constructor(private val repository: FriendsRepository) {
    suspend operator fun invoke(requestId: Long): Result<Long> = repository.rejectFriendRequest(requestId = requestId)
}