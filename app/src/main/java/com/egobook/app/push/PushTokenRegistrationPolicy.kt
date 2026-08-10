package com.egobook.app.push

/**
 * FCM 토큰을 서버에 등록할지 판단하는 정책.
 *
 * 홈 진입마다 동일한 토큰을 반복 전송하지 않도록,
 * 마지막으로 등록에 성공한 토큰과 다를 때만 등록한다.
 */
object PushTokenRegistrationPolicy {
    fun shouldRegister(
        currentToken: String,
        lastRegisteredToken: String?,
    ): Boolean {
        if (currentToken.isBlank()) return false

        return currentToken != lastRegisteredToken
    }
}
