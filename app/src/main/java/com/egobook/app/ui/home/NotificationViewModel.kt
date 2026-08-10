package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
import com.egobook.app.ui.home.notification.Notification
import com.egobook.app.ui.home.repository.HomeNotificationRepository
import com.egobook.app.ui.home.repository.NotificationSettingDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: HomeNotificationRepository,
    private val analyticsLogger: AnalyticsLogger
): ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _notificationSettingState = MutableStateFlow<NotificationSettingDto>(NotificationSettingDto(true))
    val notificationSettingState: StateFlow<NotificationSettingDto> = _notificationSettingState.asStateFlow()

    init {
        loadNotificationSetting()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val updatedList = mutableListOf<Notification>()
            repository.loadNotifications()
                .collect { notification ->
                    // 3. 데이터가 올 때마다 리스트에 추가하고 StateFlow 업데이트
                    updatedList.add(notification)
                    _notifications.value = updatedList.toList()

                }
        }
    }

    fun loadNotificationSetting() {
        viewModelScope.launch {
            val notificationSetting = repository.loadNotificationSetting()
            Timber.d("알림 설정 조회: enabled=${notificationSetting.enabled}")
            _notificationSettingState.value = notificationSetting
        }
    }

    fun changeNotificationSetting() {
        viewModelScope.launch {
            val notificationSetting = repository.changeNotificationSetting()
            Timber.d("알림 설정 변경: enabled=${notificationSetting.enabled}")
            _notificationSettingState.value = notificationSetting
            analyticsLogger.logEvent(
                AnalyticsEvent.NOTIFICATION_TOGGLE,
                mapOf(AnalyticsParam.ENABLED to notificationSetting.enabled)
            )
        }
    }

    fun readNotification(notification: Notification) {
        viewModelScope.launch {
            val notificationReadingDto = repository.readNotification(notification)
            Timber.d("알림 읽음 처리 완료: id=${notification.id}")
            analyticsLogger.logEvent(
                AnalyticsEvent.NOTIFICATION_OPEN,
                mapOf(AnalyticsParam.NOTIFICATION_TYPE to (notification.type::class.simpleName ?: "unknown"))
            )
            loadNotifications()
        }
    }
}
