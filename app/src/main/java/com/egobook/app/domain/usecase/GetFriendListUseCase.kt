package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.FriendList
import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class GetFriendListUseCase @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(): Result<FriendList> = repository.fetchFriendList()
}