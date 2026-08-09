package com.egobook.app.ui.home.notification

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationRedDotStateTest {

    @Test
    fun `읽지 않은 알림이 없으면 레드닷을 표시하지 않는다`() {
        val state = NotificationRedDotState().refreshed(
            listOf(notification(id = 1, status = NotificationStatus.READ))
        )

        assertThat(state.isVisible).isFalse()
    }

    @Test
    fun `읽지 않은 알림이 있으면 레드닷을 표시한다`() {
        val state = NotificationRedDotState().refreshed(
            listOf(
                notification(id = 1, status = NotificationStatus.READ),
                notification(id = 2, status = NotificationStatus.UNREAD)
            )
        )

        assertThat(state.isVisible).isTrue()
    }

    @Test
    fun `레드닷을 해제하면 표시하지 않는다`() {
        val state = NotificationRedDotState()
            .refreshed(listOf(notification(id = 1, status = NotificationStatus.UNREAD)))
            .dismissed()

        assertThat(state.isVisible).isFalse()
    }

    @Test
    fun `해제한 알림이 여전히 읽지 않은 상태여도 다시 표시하지 않는다`() {
        val notifications = listOf(notification(id = 1, status = NotificationStatus.UNREAD))
        val state = NotificationRedDotState()
            .refreshed(notifications)
            .dismissed()
            .refreshed(notifications)

        assertThat(state.isVisible).isFalse()
    }

    @Test
    fun `해제한 뒤 새로운 알림이 오면 다시 표시한다`() {
        val state = NotificationRedDotState()
            .refreshed(listOf(notification(id = 1, status = NotificationStatus.UNREAD)))
            .dismissed()
            .refreshed(
                listOf(
                    notification(id = 1, status = NotificationStatus.UNREAD),
                    notification(id = 2, status = NotificationStatus.UNREAD)
                )
            )

        assertThat(state.isVisible).isTrue()
    }

    @Test
    fun `읽지 않은 알림이 없는 상태에서 해제해도 이전 해제 기준을 유지한다`() {
        val state = NotificationRedDotState()
            .refreshed(listOf(notification(id = 2, status = NotificationStatus.UNREAD)))
            .dismissed()
            .refreshed(listOf(notification(id = 2, status = NotificationStatus.READ)))
            .dismissed()
            .refreshed(listOf(notification(id = 2, status = NotificationStatus.UNREAD)))

        assertThat(state.isVisible).isFalse()
    }

    private fun notification(id: Int, status: NotificationStatus) = Notification(
        id = id,
        content = "내용 $id",
        type = NotificationType.Letter,
        status = status,
        publisher = NotificationPublisher.Admin,
        publishedDate = NotificationTime(LocalDateTime.of(2026, 8, 9, 12, 0))
    )
}
