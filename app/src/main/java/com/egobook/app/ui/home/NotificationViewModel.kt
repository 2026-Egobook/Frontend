package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import com.egobook.app.ui.home.notification.EgoRoomType
import com.egobook.app.ui.home.notification.Notification
import com.egobook.app.ui.home.notification.NotificationPublisher
import com.egobook.app.ui.home.notification.NotificationStatus
import com.egobook.app.ui.home.notification.NotificationTime
import com.egobook.app.ui.home.notification.NotificationType
import java.time.LocalDateTime

class NotificationViewModel: ViewModel() {
    fun loadNotifications(): List<Notification> {
        return listOf(
            Notification(
                "답장에관한 내용이 들어가는 자리",
                NotificationType.Letter,
                NotificationStatus.UNREAD,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "답장에관한 내용이 들어가는 자리",
                NotificationType.Letter,
                NotificationStatus.READ,
                NotificationPublisher.User("jan_gu", "철수철수"),
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "지난주 주간 리포트가 도착했어요!",
                NotificationType.EgoRoom(EgoRoomType.WEAKLY_REPORT),
                NotificationStatus.READ,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "11.17 일간 칭찬서가 도착했어요!",
                NotificationType.EgoRoom(EgoRoomType.DAILY_PRAISE),
                NotificationStatus.UNREAD,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2025, 11, 17, 0, 1))
            ),
        )
    }
}
