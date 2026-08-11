package com.egobook.app.domain.model.notice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NoticePolicyTest {

    @Test
    fun `발행된 공지가 없으면 공지를 열 수 없다`() {
        assertThat(NoticePolicy.resolve(null)).isEqualTo(NoticeOpenResult.Failure)
    }

    @Test
    fun `공지 주소가 비어 있으면 공지를 열 수 없다`() {
        assertThat(NoticePolicy.resolve(notice(url = "   "))).isEqualTo(NoticeOpenResult.Failure)
    }

    @Test
    fun `https 공지 주소는 공지를 열 수 있다`() {
        assertThat(NoticePolicy.resolve(notice(url = "https://egobook.notion.site/notice")))
            .isEqualTo(NoticeOpenResult.Success("https://egobook.notion.site/notice"))
    }

    @Test
    fun `http 공지 주소는 공지를 열 수 있다`() {
        assertThat(NoticePolicy.resolve(notice(url = "http://egobook.notion.site/notice")))
            .isEqualTo(NoticeOpenResult.Success("http://egobook.notion.site/notice"))
    }

    @Test
    fun `공지 주소 앞뒤 공백은 제거한다`() {
        assertThat(NoticePolicy.resolve(notice(url = "  https://egobook.notion.site/notice  ")))
            .isEqualTo(NoticeOpenResult.Success("https://egobook.notion.site/notice"))
    }

    @Test
    fun `http 계열이 아닌 주소는 공지를 열 수 없다`() {
        assertThat(NoticePolicy.resolve(notice(url = "javascript:alert(1)")))
            .isEqualTo(NoticeOpenResult.Failure)
    }

    private fun notice(url: String) = Notice(id = 1, title = "8월 업데이트 안내", url = url)
}
