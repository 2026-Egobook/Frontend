package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(requestId: Long): Result<Long> = repository.acceptFriendRequest(requestId = requestId)
}