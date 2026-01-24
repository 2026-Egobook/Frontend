package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.repository.SquareRepository
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val repository: SquareRepository
) {
    suspend operator fun invoke(deleteId: Int): Result<Int> = repository.deleteFriend(deleteId)
}