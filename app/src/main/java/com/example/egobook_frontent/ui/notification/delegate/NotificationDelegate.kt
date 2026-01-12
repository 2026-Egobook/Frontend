package com.example.egobook_frontent.ui.notification.delegate

import com.example.egobook_frontent.domain.usecase.GetNotificationStatusUseCase
import com.example.egobook_frontent.ui.notification.model.NotificationModel
import com.example.egobook_frontent.ui.notification.model.toPresentation
import com.example.egobook_frontent.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NotificationDelegate @Inject constructor(private val getNotificationStatusUseCase: GetNotificationStatusUseCase) {
    private val _notificationStatus = MutableStateFlow<UiState<NotificationModel>>(UiState.Idle)
    val notificationStatus = _notificationStatus.asStateFlow()

    suspend fun fetchNotificationStatus() {
        _notificationStatus.value = UiState.Loading
        getNotificationStatusUseCase().onSuccess { domain ->
            _notificationStatus.value = UiState.Success(domain.toPresentation())
        }.onFailure { error ->
            _notificationStatus.value = UiState.Failure(error.message)
        }
    }
}