package com.example.egobook_frontent.ui.notification.delegate

import com.example.egobook_frontent.domain.model.NotificationType
import com.example.egobook_frontent.domain.usecase.GetNotificationStatusUseCase
import com.example.egobook_frontent.domain.usecase.UpdateNotificationUseCase
import com.example.egobook_frontent.ui.notification.model.NotificationModel
import com.example.egobook_frontent.ui.notification.model.toPresentation
import com.example.egobook_frontent.util.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NotificationDelegate @Inject constructor(
    private val getNotificationStatusUseCase: GetNotificationStatusUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase
) {
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

    private val _updateNotificationResult = MutableSharedFlow<UiState<Boolean>>()
    val updateNotificationResult = _updateNotificationResult.asSharedFlow()

    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean) {
        _updateNotificationResult.emit(UiState.Loading)
        updateNotificationUseCase(type = type, isEnabled = isEnabled).onSuccess { updateStatus ->
            _updateNotificationResult.emit(UiState.Success(updateStatus))
        }.onFailure { error ->
            _updateNotificationResult.emit(UiState.Failure(error.message))
        }
    }
}