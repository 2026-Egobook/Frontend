package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.SearchUser
import com.egobook.app.domain.repository.FriendsRepository
import javax.inject.Inject

class SearchUserUseCase @Inject constructor(private val repository: FriendsRepository){
    suspend operator fun invoke(keyword: String): Result<List<SearchUser>?> = repository.searchUser(keyword = keyword)
}