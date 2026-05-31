package com.egobook.app.store.ui

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PreviewExpansionStateTest {

    @Test
    fun `초기 상태는 COLLAPSED다`() {
        val state = PreviewExpansionState.COLLAPSED

        assertThat(state.isExpanded).isFalse()
    }

    @Test
    fun `COLLAPSED에서 toggle하면 EXPANDED가 된다`() {
        val state = PreviewExpansionState.COLLAPSED.toggle()

        assertThat(state).isEqualTo(PreviewExpansionState.EXPANDED)
        assertThat(state.isExpanded).isTrue()
    }

    @Test
    fun `EXPANDED에서 toggle하면 COLLAPSED로 돌아온다`() {
        val state = PreviewExpansionState.EXPANDED.toggle()

        assertThat(state).isEqualTo(PreviewExpansionState.COLLAPSED)
        assertThat(state.isExpanded).isFalse()
    }

    @Test
    fun `두 번 toggle하면 원래 상태로 돌아온다`() {
        val original = PreviewExpansionState.COLLAPSED
        val result = original.toggle().toggle()

        assertThat(result).isEqualTo(original)
    }
}
