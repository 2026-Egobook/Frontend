package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterPaperItem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class LetterPaperItemTest {

    @Test
    fun `Pink URL은 PINK 색상으로 매핑된다`() {
        val item = LetterPaperItem.fromDto(
            id = 39,
            price = 75,
            isPurchased = false,
            shopImageUrl ="https://dev-img.egobook.site/letter/letter/Pink.png"
        )
        assertThat(item.color).isEqualTo(LetterBackgroundColor.PINK)
    }

    @Test
    fun `Green URL은 GREEN 색상으로 매핑된다`() {
        val item = LetterPaperItem.fromDto(
            id = 38,
            price = 100,
            isPurchased = false,
            shopImageUrl ="https://dev-img.egobook.site/letter/letter/Green.png"
        )
        assertThat(item.color).isEqualTo(LetterBackgroundColor.GREEN)
    }

    @Test
    fun `Blue URL은 BLUE 색상으로 매핑된다`() {
        val item = LetterPaperItem.fromDto(
            id = 37,
            price = 150,
            isPurchased = false,
            shopImageUrl ="https://dev-img.egobook.site/letter/letter/Blue.png"
        )
        assertThat(item.color).isEqualTo(LetterBackgroundColor.BLUE)
    }

    @Test
    fun `Purple URL은 PURPLE 색상으로 매핑된다`() {
        val item = LetterPaperItem.fromDto(
            id = 40,
            price = 175,
            isPurchased = false,
            shopImageUrl ="https://dev-img.egobook.site/letter/letter/Purple.png"
        )
        assertThat(item.color).isEqualTo(LetterBackgroundColor.PURPLE)
    }

    @Test
    fun `isPurchased가 true인 아이템은 구매 상태를 유지한다`() {
        val item = LetterPaperItem.fromDto(
            id = 39,
            price = 75,
            isPurchased = true,
            shopImageUrl ="https://dev-img.egobook.site/letter/letter/Pink.png"
        )
        assertThat(item.isPurchased).isTrue()
    }

    @Test
    fun `알 수 없는 색상 URL은 예외를 발생시킨다`() {
        assertThatThrownBy {
            LetterPaperItem.fromDto(
                id = 99,
                price = 100,
                isPurchased = false,
                shopImageUrl ="https://dev-img.egobook.site/letter/letter/Unknown.png"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
