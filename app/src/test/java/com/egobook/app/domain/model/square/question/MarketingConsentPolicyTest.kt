package com.egobook.app.domain.model.square.question

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarketingConsentPolicyTest {

    @Test
    fun `미동의 상태에서 동의로 전환하면 확인이 필요하다`() {
        assertThat(MarketingConsentPolicy.requiresConfirmation(currentlyEnabled = false, requestedEnabled = true))
            .isTrue()
    }

    @Test
    fun `동의 상태에서 비동의로 전환하면 확인이 필요없다`() {
        assertThat(MarketingConsentPolicy.requiresConfirmation(currentlyEnabled = true, requestedEnabled = false))
            .isFalse()
    }

    @Test
    fun `동의 상태에서 다시 동의를 요청해도 확인이 필요없다`() {
        assertThat(MarketingConsentPolicy.requiresConfirmation(currentlyEnabled = true, requestedEnabled = true))
            .isFalse()
    }

    @Test
    fun `미동의 상태에서 다시 비동의를 요청해도 확인이 필요없다`() {
        assertThat(MarketingConsentPolicy.requiresConfirmation(currentlyEnabled = false, requestedEnabled = false))
            .isFalse()
    }
}
