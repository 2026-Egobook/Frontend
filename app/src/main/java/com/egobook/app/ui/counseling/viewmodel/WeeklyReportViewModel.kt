package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.usecase.GetWeeklyReportStyleUseCase
import com.egobook.app.domain.usecase.GetWeeklyReportUseCase
import com.egobook.app.domain.usecase.UpdateWeeklyReportStyleUseCase
import com.egobook.app.ui.counseling.model.WeeklyReportModel
import com.egobook.app.ui.counseling.model.WeeklyReportStyleModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.ui.notification.delegate.NotificationDelegate
import com.egobook.app.ui.notification.model.NotificationModel
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeeklyReportViewModel @Inject constructor(
    private val getWeeklyReportUseCase: GetWeeklyReportUseCase,
    private val getWeeklyReportStyleUseCase: GetWeeklyReportStyleUseCase,
    private val updateWeeklyReportStyleUseCase: UpdateWeeklyReportStyleUseCase,
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

    private val _weeklyReportStyle = MutableStateFlow<UiState<WeeklyReportStyleModel>>(UiState.Idle)
    val weeklyReportStyle = _weeklyReportStyle.asStateFlow()

    fun fetchWeeklyReportStyle() {
        viewModelScope.launch {
            getWeeklyReportStyleUseCase().onSuccess { domainStyle ->
                _weeklyReportStyle.value = UiState.Success(domainStyle.toPresentation())
            }.onFailure { error ->
                _weeklyReportStyle.value = UiState.Failure(error.message)
            }
        }
    }

    private val _updateReportStyleResult = MutableSharedFlow<UiState<ReportStyle>>()
    val updateReportStyleResult = _updateReportStyleResult.asSharedFlow()

    fun updateWeeklyReportStyle(reportStyle: ReportStyle) {
        viewModelScope.launch {
            _updateReportStyleResult.emit(UiState.Loading)
            updateWeeklyReportStyleUseCase(reportStyle = reportStyle).onSuccess { reportStyle ->
                _updateReportStyleResult.emit( UiState.Success(reportStyle))
            }.onFailure { error ->
                _updateReportStyleResult.emit( UiState.Failure(error.message))
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