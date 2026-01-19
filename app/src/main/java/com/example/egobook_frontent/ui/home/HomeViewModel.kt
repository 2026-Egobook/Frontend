package com.example.egobook_frontent.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserState(Level(1), Ink(9999)))
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()

    fun loadNotifications(): List<Notification> {
        return listOf(
            Notification(
                "답장에관한 내용이 들어가는 자리",
                NotificationType.LETTER,
                NotificationStatus.UNREAD,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "답장에관한 내용이 들어가는 자리",
                NotificationType.LETTER,
                NotificationStatus.READ,
                NotificationPublisher.User("jan_gu", "철수철수"),
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "지난주 주간 리포트가 도착했어요!",
                NotificationType.EGO_ROOM,
                NotificationStatus.READ,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2026, 1, 19, 13, 53))
            ),
            Notification(
                "11.17 일간 칭찬서가 도착했어요!",
                NotificationType.EGO_ROOM,
                NotificationStatus.UNREAD,
                NotificationPublisher.Admin,
                NotificationTime(LocalDateTime.of(2025, 11, 17, 0, 1))
            ),
        )
    }
}
