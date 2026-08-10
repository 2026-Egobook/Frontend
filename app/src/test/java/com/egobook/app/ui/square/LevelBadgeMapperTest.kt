package com.egobook.app.ui.square

import com.egobook.app.R
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class LevelBadgeMapperTest {
    @Test
    fun `level thresholds map to existing badge drawables`() {
        assertThat(levelBadgeDrawable(0)).isEqualTo(R.drawable.level_type_1)
        assertThat(levelBadgeDrawable(100)).isEqualTo(R.drawable.level_type_2)
        assertThat(levelBadgeDrawable(300)).isEqualTo(R.drawable.level_type_3)
        assertThat(levelBadgeDrawable(500)).isEqualTo(R.drawable.level_type_4)
        assertThat(levelBadgeDrawable(700)).isEqualTo(R.drawable.level_type_5)
        assertThat(levelBadgeDrawable(1000)).isEqualTo(R.drawable.level_type_6)
        assertThat(levelBadgeDrawable(1300)).isEqualTo(R.drawable.level_type_7)
        assertThat(levelBadgeDrawable(1500)).isEqualTo(R.drawable.level_type_8)
    }

    @Test
    fun `levels outside the shared domain range are rejected`() {
        assertThatThrownBy { levelBadgeDrawable(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { levelBadgeDrawable(1501) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
