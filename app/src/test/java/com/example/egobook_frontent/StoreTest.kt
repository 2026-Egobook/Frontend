package com.example.egobook_frontent

import com.example.egobook_frontent.ui.shop.Price
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class StoreTest {
    @Test
    fun `아이템 가격은 음수가 될 수 없다`() {
        assertThatThrownBy { Price(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
