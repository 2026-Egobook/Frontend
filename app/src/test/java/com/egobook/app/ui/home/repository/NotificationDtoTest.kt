package com.egobook.app.ui.home.repository

import com.egobook.app.ui.home.notification.NotificationType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotificationDtoTest {

    @Test
    fun `공지 알림은 공지 타입과 링크 주소를 그대로 가진다`() {
        val notification = dto(type = "NOTICE", linkUrl = "https://egobook.notion.site/notice").toDomainOrNull()

        assertThat(notification?.type).isEqualTo(NotificationType.Notice)
        assertThat(notification?.linkUrl).isEqualTo("https://egobook.notion.site/notice")
    }

    @Test
    fun `공지 알림에 내용이 없으면 제목을 내용으로 사용한다`() {
        val notification = dto(type = "NOTICE", content = null, title = "8월 업데이트 안내").toDomainOrNull()

        assertThat(notification?.content).isEqualTo("8월 업데이트 안내")
    }

    @Test
    fun `알 수 없는 타입의 알림은 목록에서 제외한다`() {
        assertThat(dto(type = "UNKNOWN_TYPE").toDomainOrNull()).isNull()
    }

    private fun dto(
        type: String,
        title: String = "제목",
        content: String? = "내용",
        linkUrl: String? = null
    ) = NotificationDto(
        notificationId = 1,
        type = type,
        title = title,
        content = content,
        isRead = false,
        targetId = 1,
        createdAt = "2026-08-09T15:49:00.618652",
        linkUrl = linkUrl
    )
}
