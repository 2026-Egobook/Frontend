package com.egobook.app.push

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PushTokenRegistrationPolicyTest {
    @Test
    fun `등록한 적이 없으면 서버에 등록한다`() {
        val shouldRegister =
            PushTokenRegistrationPolicy.shouldRegister(
                currentToken = "token-a",
                lastRegisteredToken = null,
            )

        assertThat(shouldRegister).isTrue()
    }

    @Test
    fun `토큰이 갱신되었으면 서버에 다시 등록한다`() {
        val shouldRegister =
            PushTokenRegistrationPolicy.shouldRegister(
                currentToken = "token-b",
                lastRegisteredToken = "token-a",
            )

        assertThat(shouldRegister).isTrue()
    }

    @Test
    fun `이미 등록한 토큰과 같으면 다시 등록하지 않는다`() {
        val shouldRegister =
            PushTokenRegistrationPolicy.shouldRegister(
                currentToken = "token-a",
                lastRegisteredToken = "token-a",
            )

        assertThat(shouldRegister).isFalse()
    }

    @Test
    fun `토큰이 비어 있으면 등록하지 않는다`() {
        val shouldRegister =
            PushTokenRegistrationPolicy.shouldRegister(
                currentToken = "",
                lastRegisteredToken = null,
            )

        assertThat(shouldRegister).isFalse()
    }

    @Test
    fun `토큰이 공백 문자로만 이루어져 있으면 등록하지 않는다`() {
        val shouldRegister =
            PushTokenRegistrationPolicy.shouldRegister(
                currentToken = "   ",
                lastRegisteredToken = "token-a",
            )

        assertThat(shouldRegister).isFalse()
    }
}
