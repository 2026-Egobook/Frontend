package com.egobook.app.ui.home.notification

/** 알림 목록에서 공지(NOTICE) 알림을 골라내는 정책. */
object NoticeNotificationPolicy {

    fun latestNotice(notifications: List<Notification>): Notification? =
        notifications.filter { it.type is NotificationType.Notice }
            .maxWithOrNull(compareBy({ it.publishedDate.time }, { it.id }))

    fun hasUnreadNotice(notifications: List<Notification>): Boolean =
        notifications.any { it.type is NotificationType.Notice && it.status == NotificationStatus.UNREAD }
}
