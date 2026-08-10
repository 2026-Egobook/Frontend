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

    @Test
    fun `only the plaza default turtle is mirrored horizontally`() {
        assertThat(profileTurtleScaleX(null, mirrorFallback = true)).isEqualTo(-1f)
        assertThat(profileTurtleScaleX(null, mirrorFallback = false)).isEqualTo(1f)
        assertThat(profileTurtleScaleX("https://example.com/profile.png", mirrorFallback = true)).isEqualTo(1f)
        assertThat(profileTurtleScaleX("https://example.com/profile.png", mirrorFallback = true, isFallbackDisplayed = true))
            .isEqualTo(-1f)
        assertThat(profileTurtleScaleX("https://example.com/profile.png", mirrorFallback = false, isFallbackDisplayed = true))
            .isEqualTo(1f)
    }
}
