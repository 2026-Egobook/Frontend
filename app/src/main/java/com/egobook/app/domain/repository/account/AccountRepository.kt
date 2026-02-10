package com.egobook.app.domain.repository.account

interface AccountRepository {

    /**
     * 유저 id 조회
     */
    suspend fun getUserId(): Result<String>
}