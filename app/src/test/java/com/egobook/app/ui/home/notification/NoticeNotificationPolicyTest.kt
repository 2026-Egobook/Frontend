package com.egobook.app.ui.home.notification

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NoticeNotificationPolicyTest {

    @Test
    fun `공지 알림이 없으면 최신 공지도 없다`() {
        val notifications = listOf(letter(id = 1), egoRoom(id = 2))

        assertThat(NoticeNotificationPolicy.latestNotice(notifications)).isNull()
    }

    @Test
    fun `가장 최근에 발행된 공지를 최신 공지로 고른다`() {
        val old = notice(id = 1, publishedAt = LocalDateTime.of(2026, 8, 1, 9, 0))
        val latest = notice(id = 2, publishedAt = LocalDateTime.of(2026, 8, 9, 9, 0))

        assertThat(NoticeNotificationPolicy.latestNotice(listOf(old, latest))).isEqualTo(latest)
    }

    @Test
    fun `발행 시각이 같으면 나중에 생성된 공지를 고른다`() {
        val sameTime = LocalDateTime.of(2026, 8, 9, 9, 0)
        val older = notice(id = 1, publishedAt = sameTime)
        val newer = notice(id = 2, publishedAt = sameTime)

        assertThat(NoticeNotificationPolicy.latestNotice(listOf(newer, older))).isEqualTo(newer)
    }

    @Test
    fun `읽지 않은 공지가 있으면 레드닷을 표시한다`() {
        val notifications = listOf(notice(id = 1, status = NotificationStatus.UNREAD))

        assertThat(NoticeNotificationPolicy.hasUnreadNotice(notifications)).isTrue()
    }

    @Test
    fun `공지를 모두 읽었으면 레드닷을 표시하지 않는다`() {
        val notifications = listOf(
            notice(id = 1, status = NotificationStatus.READ),
            notice(id = 2, status = NotificationStatus.READ)
        )

        assertThat(NoticeNotificationPolicy.hasUnreadNotice(notifications)).isFalse()
    }

    @Test
    fun `읽지 않은 알림이 공지가 아니면 레드닷을 표시하지 않는다`() {
        val notifications = listOf(
            letter(id = 1, status = NotificationStatus.UNREAD),
            egoRoom(id = 2, status = NotificationStatus.UNREAD)
        )

        assertThat(NoticeNotificationPolicy.hasUnreadNotice(notifications)).isFalse()
    }

    private fun notice(
        id: Int,
        status: NotificationStatus = NotificationStatus.UNREAD,
        publishedAt: LocalDateTime = LocalDateTime.of(2026, 8, 9, 9, 0)
    ) = notification(id, NotificationType.Notice, status, publishedAt)

    private fun letter(
        id: Int,
        status: NotificationStatus = NotificationStatus.READ,
        publishedAt: LocalDateTime = LocalDateTime.of(2026, 8, 9, 9, 0)
    ) = notification(id, NotificationType.Letter, status, publishedAt)

    private fun egoRoom(
        id: Int,
        status: NotificationStatus = NotificationStatus.READ,
        publishedAt: LocalDateTime = LocalDateTime.of(2026, 8, 9, 9, 0)
    ) = notification(id, NotificationType.EgoRoom(EgoRoomType.DAILY_PRAISE), status, publishedAt)

    private fun notification(
        id: Int,
        type: NotificationType,
        status: NotificationStatus,
        publishedAt: LocalDateTime
    ) = Notification(
        id = id,
        content = "내용",
        type = type,
        status = status,
        publisher = NotificationPublisher.Admin,
        publishedDate = NotificationTime(publishedAt),
        linkUrl = "https://egobook.notion.site/notice"
    )
}
