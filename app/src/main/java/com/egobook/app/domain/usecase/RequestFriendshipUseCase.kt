package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class RequestFriendshipUseCase @Inject constructor(private val repository: FriendsRepository) {
    suspend operator fun invoke(receiverId: Long): Result<Unit> = repository.requestFriendship(receiverId = receiverId)
}