package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.FriendRequest
import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class GetOutgoingFriendRequestsUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(): Result<List<FriendRequest>> = repository.fetchOutgoingFriendRequestList()
}