package com.egobook.app.ui.notification.delegate

import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.usecase.GetNotificationStatusUseCase
import com.egobook.app.domain.usecase.UpdateNotificationUseCase
import com.egobook.app.ui.notification.model.NotificationModel
import com.egobook.app.ui.notification.model.toPresentation
import com.egobook.app.util.UiState
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