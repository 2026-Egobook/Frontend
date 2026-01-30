package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(deleteId: Long): Result<Long> = repository.deleteFriend(deleteId)
}