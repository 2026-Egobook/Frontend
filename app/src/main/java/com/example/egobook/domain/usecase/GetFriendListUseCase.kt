package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.Friend
import com.example.egobook.domain.repository.SquareRepository
import javax.inject.Inject

class GetFriendListUseCase @Inject constructor(
    private val repository: SquareRepository
) {
    suspend operator fun invoke(): Result<List<Friend>> = repository.fetchFriendList()

}