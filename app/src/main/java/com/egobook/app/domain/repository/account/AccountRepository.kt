package com.egobook.app.domain.repository.account

interface AccountRepository {

    /**
     * 유저 id 조회
     */
    suspend fun getUserId(): Result<String>

    /**
     * 구글 계정 연동
     */
    suspend fun linkToGoogle(idToken: String): Result<Unit>


}