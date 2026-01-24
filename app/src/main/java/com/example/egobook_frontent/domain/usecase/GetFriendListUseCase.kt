package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.Friend
import com.example.egobook_frontent.domain.repository.SquareRepository
import javax.inject.Inject

class GetFriendListUseCase @Inject constructor(
    private val repository: SquareRepository
) {
    suspend operator fun invoke(): Result<List<Friend>> = repository.fetchFriendList()

}