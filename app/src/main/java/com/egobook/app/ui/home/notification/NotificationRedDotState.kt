package com.egobook.app.ui.home.notification

/**
 * 홈 벨 아이콘 레드닷 노출 상태.
 *
 * 읽지 않은 알림 중 가장 최근 알림 id가 마지막으로 해제한 기준보다 클 때만 레드닷을 표시한다.
 * 해제 기준은 앱 실행 중에만 유지되므로, 재시작 후 미읽음 알림이 남아 있으면 다시 표시된다.
 */
data class NotificationRedDotState(
    private val latestUnreadId: Int? = null,
    private val dismissedUpToId: Int? = null
) {
    val isVisible: Boolean
        get() = latestUnreadId != null && (dismissedUpToId == null || latestUnreadId > dismissedUpToId)

    fun refreshed(notifications: List<Notification>): NotificationRedDotState = copy(
        latestUnreadId = notifications
            .filter { it.status == NotificationStatus.UNREAD }
            .maxOfOrNull { it.id }
    )

    fun dismissed(): NotificationRedDotState = copy(dismissedUpToId = latestUnreadId ?: dismissedUpToId)
}
