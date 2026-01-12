package com.example.egobook_frontent.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.egobook_frontent.domain.model.NotificationType
import com.example.egobook_frontent.domain.usecase.GetWeeklyReportUseCase
import com.example.egobook_frontent.ui.counseling.model.WeeklyReportModel
import com.example.egobook_frontent.ui.counseling.model.toPresentation
import com.example.egobook_frontent.ui.notification.delegate.NotificationDelegate
import com.example.egobook_frontent.ui.notification.model.NotificationModel
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeeklyReportViewModel @Inject constructor(
    private val getWeeklyReportUseCase: GetWeeklyReportUseCase,
    private val notificationDelegate: NotificationDelegate
): ViewModel() {

    private val _weeklyReportList = MutableStateFlow<UiState<List<WeeklyReportModel>>>(UiState.Idle)
    val weeklyReportList = _weeklyReportList.asStateFlow()

    fun fetchWeeklyReport() {
        viewModelScope.launch {
            _weeklyReportList.value = UiState.Loading
            getWeeklyReportUseCase().onSuccess { domainList ->
                _weeklyReportList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _weeklyReportList.value = UiState.Failure(error.message)
            }
        }
    }

    val notificationStatus: StateFlow<UiState<NotificationModel>> = notificationDelegate.notificationStatus

    fun fetchNotificationStatus() {
        viewModelScope.launch {
            notificationDelegate.fetchNotificationStatus()
        }
    }

    val updateNotificationResult: SharedFlow<UiState<Boolean>> = notificationDelegate.updateNotificationResult

    fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean) {
        viewModelScope.launch {
            notificationDelegate.updateNotificationStatus(type = type, isEnabled = isEnabled)
        }
    }
}