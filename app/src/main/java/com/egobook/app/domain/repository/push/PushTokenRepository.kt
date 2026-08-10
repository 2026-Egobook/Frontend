package com.egobook.app.domain.repository.push

interface PushTokenRepository {
    /**
     * 현재 기기의 FCM 토큰을 서버에 등록한다.
     * 이미 등록된 토큰과 동일하면 서버를 호출하지 않고 성공으로 처리한다.
     */
    suspend fun registerCurrentToken(): Result<Unit>
}
