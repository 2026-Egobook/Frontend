package com.egobook.app.ui.square

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProfileImageSourcePolicyTest {
    @Test
    fun `custom placement is applied only to a remote profile image`() {
        assertThat(hasRemoteProfileImage(null)).isFalse()
        assertThat(hasRemoteProfileImage("")).isFalse()
        assertThat(hasRemoteProfileImage("   ")).isFalse()
        assertThat(hasRemoteProfileImage("https://example.com/profile.png")).isTrue()
    }
}
