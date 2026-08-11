package com.egobook.app.ui.home.repository

import com.egobook.app.domain.model.notice.Notice
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NoticeDtoTest {

    @Test
    fun `최신 공지 응답을 공지 모델로 변환한다`() {
        val dto = NoticeDto(
            noticeId = 7,
            title = "8월 업데이트 안내",
            notionUrl = "https://egobook.notion.site/notice",
            publishedAt = "2026-08-11T00:00:00"
        )

        assertThat(dto.toDomain()).isEqualTo(
            Notice(id = 7, title = "8월 업데이트 안내", url = "https://egobook.notion.site/notice")
        )
    }

    @Test
    fun `공지 주소가 없으면 주소 없는 공지로 변환한다`() {
        val dto = NoticeDto(
            noticeId = 7,
            title = "8월 업데이트 안내",
            notionUrl = null,
            publishedAt = "2026-08-11T00:00:00"
        )

        assertThat(dto.toDomain().url).isNull()
    }
}
