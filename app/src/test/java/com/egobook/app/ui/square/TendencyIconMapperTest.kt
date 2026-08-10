package com.egobook.app.ui.square

import com.egobook.app.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Locale

class TendencyIconMapperTest {
    @Test
    fun `question ability names map to radar icons`() {
        assertThat(tendencyIconDrawable("공감성")).isEqualTo(R.drawable.ic_radar_heart)
        assertThat(tendencyIconDrawable("자존감")).isEqualTo(R.drawable.ic_radar_diamond)
        assertThat(tendencyIconDrawable("성실성")).isEqualTo(R.drawable.ic_radar_clover)
        assertThat(tendencyIconDrawable("긍정사고")).isEqualTo(R.drawable.ic_radar_sun)
        assertThat(tendencyIconDrawable("감정조절")).isEqualTo(R.drawable.ic_radar_star)
    }

    @Test
    fun `enum codes and unknown values have stable mappings`() {
        assertThat(tendencyIconDrawable("POSITIVE_THINKING")).isEqualTo(R.drawable.ic_radar_sun)
        assertThat(tendencyIconDrawable(null)).isEqualTo(R.drawable.ic_square_friend_answer_star)
        assertThat(tendencyIconDrawable("unknown")).isEqualTo(R.drawable.ic_square_friend_answer_star)
    }

    @Test
    fun `lowercase enum code mapping does not depend on device locale`() {
        val previousLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"))
            assertThat(tendencyIconDrawable("diligence")).isEqualTo(R.drawable.ic_radar_clover)
        } finally {
            Locale.setDefault(previousLocale)
        }
    }
}
