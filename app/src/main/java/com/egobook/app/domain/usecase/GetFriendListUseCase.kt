package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.Friend
import com.egobook.app.domain.repository.SquareRepository
import javax.inject.Inject

class GetFriendListUseCase @Inject constructor(
    private val repository: SquareRepository
) {
    suspend operator fun invoke(): Result<List<Friend>> = repository.fetchFriendList()

}