package com.egobook.app.ui.home.ui

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RadarHelpContentTest {
    @Test
    fun `레이더 도움말은 다섯 성향의 상승 조건을 모두 포함한다`() {
        val content = RadarHelpContent.buildText()

        assertThat(content).contains("5칸을 모두 채우면 1레벨이 상승합니다.")
        assertThat(content).contains("공감성 (하루 한 번)")
        assertThat(content).contains("자존감")
        assertThat(content).contains("감정조절 (하루 한 번)")
        assertThat(content).contains("긍정사고 (하루 한 번)")
        assertThat(content).contains("성실함")
    }
}
