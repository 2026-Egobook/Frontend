package com.egobook.app.domain.repository.account

interface AccountRepository {

    /**
     * 유저 id 조회
     */
    suspend fun getUserId(forceRefresh: Boolean = false): Result<String>

    /**
     * 구글 계정 연동
     */
    suspend fun linkToGoogle(idToken: String): Result<Unit>

    /**
     * 로컬에서 게스트타입과 GOOGLE이면 email을 읽어오는 로직
     */
    suspend fun getLinkedAccountInfo(): Result<LinkedAccountInfo>

}

data class LinkedAccountInfo(
    val email: String?,
    val isGoogleLinked: Boolean  // loginType == GOOGLE
)