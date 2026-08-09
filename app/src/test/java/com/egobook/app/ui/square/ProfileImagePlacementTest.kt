package com.egobook.app.ui.square

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test

class ProfileImagePlacementTest {
    @Test
    fun `plaza turtle centers its head at sixty five percent of frame width`() {
        val result = calculateProfileImageTransform(100, 100, 304, 197, ProfileImagePlacement.PLAZA_TURTLE)

        assertThat(304f * TURTLE_HEAD_WIDTH_FRACTION * result.scale)
            .isCloseTo(65f, offset(0.01f))
        assertThat(304f * TURTLE_HEAD_CENTER_X_FRACTION * result.scale + result.translateX)
            .isCloseTo(50f, offset(0.01f))
        assertThat(197f * TURTLE_HEAD_CENTER_Y_FRACTION * result.scale + result.translateY)
            .isCloseTo(50f, offset(0.01f))
    }

    @Test
    fun `friend turtle centers a larger head at eighty five percent of frame width`() {
        val result = calculateProfileImageTransform(100, 100, 304, 197, ProfileImagePlacement.FRIEND_TURTLE)

        assertThat(304f * TURTLE_HEAD_WIDTH_FRACTION * result.scale)
            .isCloseTo(85f, offset(0.01f))
        assertThat(304f * TURTLE_HEAD_CENTER_X_FRACTION * result.scale + result.translateX)
            .isCloseTo(50f, offset(0.01f))
        assertThat(197f * TURTLE_HEAD_CENTER_Y_FRACTION * result.scale + result.translateY)
            .isCloseTo(50f, offset(0.01f))
    }

    @Test
    fun `plaza background center crops to fill the frame`() {
        val result = calculateProfileImageTransform(100, 100, 500, 1000, ProfileImagePlacement.PLAZA_BACKGROUND)

        assertThat(result.scale).isCloseTo(0.2f, offset(0.001f))
        assertThat(result.translateX).isCloseTo(0f, offset(0.001f))
        assertThat(result.translateY).isCloseTo(-50f, offset(0.001f))
    }

    @Test
    fun `friend background center crops then moves up thirteen point one percent`() {
        val result = calculateProfileImageTransform(100, 100, 500, 1000, ProfileImagePlacement.FRIEND_BACKGROUND)

        assertThat(result.scale).isCloseTo(0.2f, offset(0.001f))
        assertThat(result.translateX).isCloseTo(0f, offset(0.001f))
        assertThat(result.translateY).isCloseTo(-63.1f, offset(0.001f))
    }
}
