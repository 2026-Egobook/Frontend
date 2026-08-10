package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.notification.NotificationRedDotState
import com.egobook.app.ui.home.repository.HomeNotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class NotificationRedDotViewModel @Inject constructor(
    private val repository: HomeNotificationRepository
) : ViewModel() {

    private val _redDotState = MutableStateFlow(NotificationRedDotState())
    val redDotState: StateFlow<NotificationRedDotState> = _redDotState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            try {
                val notifications = repository.loadNotifications().toList()
                _redDotState.update { it.refreshed(notifications) }
            } catch (error: Exception) {
                Timber.e(error, "Failed to refresh unread notifications")
            }
        }
    }

    fun dismiss() {
        _redDotState.update { it.dismissed() }
    }
}
