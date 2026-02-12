package com.egobook.app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.notification.Notification
import com.egobook.app.ui.home.repository.HomeNotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: HomeNotificationRepository
): ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    fun loadNotifications() {
        Log.d("jang", "loadNotifications")
        viewModelScope.launch {
            val updatedList = mutableListOf<Notification>()
            repository.loadNotifications()
                .collect { notification ->
                    // 3. 데이터가 올 때마다 리스트에 추가하고 StateFlow 업데이트
                    updatedList.add(notification)
                    _notifications.value = updatedList.toList()

                    // 디버깅용 로그: 데이터가 실제로 오는지 확인
                    Log.d("NotificationViewModel", "새 알림 수신: ${notification.content}")
                }
        }
    }
}
