package com.egobook.app.domain.repository.account

import com.egobook.app.domain.model.account.WithdrawReasonType

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

    /**
     * 탈퇴 사유 저장
     *
     * 서버 정책상 [deleteAccount] 호출 이전에 먼저 호출해야 저장된다.
     *
     * @param text 기타(OTHER) 선택 시 입력한 상세 사유
     */
    suspend fun submitWithdrawReason(
        reasonType: WithdrawReasonType,
        text: String? = null
    ): Result<Unit>

    /**
     * 계정 탈퇴
     */
    suspend fun deleteAccount(): Result<Unit>

    /**
     * 닉네임 변경
     */
    suspend fun updateNickname(nickname: String): Result<Unit>

}

data class LinkedAccountInfo(
    val email: String?,
    val isGoogleLinked: Boolean  // loginType == GOOGLE
)