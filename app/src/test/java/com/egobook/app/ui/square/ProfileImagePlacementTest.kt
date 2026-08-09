package com.egobook.app.ui.square

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test

class ProfileImagePlacementTest {
    @Test
    fun `plaza profile placement follows design ratios`() {
        assertThat(ProfileImagePlacement.PLAZA_TURTLE.scale).isEqualTo(0.3f)
        assertThat(ProfileImagePlacement.PLAZA_TURTLE.horizontal).isEqualTo(ProfileAlignment.START)
        assertThat(ProfileImagePlacement.PLAZA_TURTLE.vertical).isEqualTo(ProfileAlignment.END)

        assertThat(ProfileImagePlacement.PLAZA_BACKGROUND.scale).isEqualTo(0.1f)
        assertThat(ProfileImagePlacement.PLAZA_BACKGROUND.horizontal).isEqualTo(ProfileAlignment.CENTER)
        assertThat(ProfileImagePlacement.PLAZA_BACKGROUND.vertical).isEqualTo(ProfileAlignment.CENTER)
    }

    @Test
    fun `friend profile placement follows design ratios`() {
        assertThat(ProfileImagePlacement.FRIEND_TURTLE.scale).isEqualTo(1f)
        assertThat(ProfileImagePlacement.FRIEND_TURTLE.horizontal).isEqualTo(ProfileAlignment.START)
        assertThat(ProfileImagePlacement.FRIEND_TURTLE.vertical).isEqualTo(ProfileAlignment.END)

        assertThat(ProfileImagePlacement.FRIEND_BACKGROUND.scale).isEqualTo(0.5f)
        assertThat(ProfileImagePlacement.FRIEND_BACKGROUND.horizontal).isEqualTo(ProfileAlignment.CENTER)
        assertThat(ProfileImagePlacement.FRIEND_BACKGROUND.vertical).isEqualTo(ProfileAlignment.CENTER)
        assertThat(ProfileImagePlacement.FRIEND_BACKGROUND.verticalOffsetFraction).isEqualTo(-0.131f)
    }

    @Test
    fun `plaza turtle is scaled from fit center and aligned start bottom`() {
        val result = calculateProfileImageTransform(100, 100, 200, 100, ProfileImagePlacement.PLAZA_TURTLE)

        assertThat(result.scale).isCloseTo(0.15f, offset(0.0001f))
        assertThat(result.translateX).isCloseTo(0f, offset(0.0001f))
        assertThat(result.translateY).isCloseTo(85f, offset(0.0001f))
    }

    @Test
    fun `plaza background is scaled and centered`() {
        val result = calculateProfileImageTransform(100, 100, 50, 100, ProfileImagePlacement.PLAZA_BACKGROUND)

        assertThat(result.scale).isCloseTo(0.1f, offset(0.0001f))
        assertThat(result.translateX).isCloseTo(47.5f, offset(0.0001f))
        assertThat(result.translateY).isCloseTo(45f, offset(0.0001f))
    }

    @Test
    fun `friend turtle keeps fit size and aligns start bottom`() {
        val result = calculateProfileImageTransform(100, 100, 200, 100, ProfileImagePlacement.FRIEND_TURTLE)

        assertThat(result.scale).isCloseTo(0.5f, offset(0.0001f))
        assertThat(result.translateX).isCloseTo(0f, offset(0.0001f))
        assertThat(result.translateY).isCloseTo(50f, offset(0.0001f))
    }

    @Test
    fun `friend background centers then offsets by thirteen point one percent of frame height`() {
        val result = calculateProfileImageTransform(200, 100, 100, 100, ProfileImagePlacement.FRIEND_BACKGROUND)

        assertThat(result.scale).isCloseTo(0.5f, offset(0.0001f))
        assertThat(result.translateX).isCloseTo(75f, offset(0.0001f))
        assertThat(result.translateY).isCloseTo(11.9f, offset(0.0001f))
    }
}
