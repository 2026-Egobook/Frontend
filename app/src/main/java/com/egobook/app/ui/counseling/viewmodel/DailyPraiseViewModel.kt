package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.usecase.GetDailyPraiseUseCase
import com.egobook.app.ui.counseling.model.PraiseMessageModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.ui.notification.delegate.NotificationDelegate
import com.egobook.app.ui.notification.model.NotificationModel
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyPraiseViewModel @Inject constructor(
    private val getDailyPraiseUseCase: GetDailyPraiseUseCase,
    private val notificationDelegate: NotificationDelegate
): ViewModel() {

    private val _dailyPraiseList = MutableStateFlow<UiState<List<PraiseMessageModel>>>(UiState.Idle)
    val dailyPraise = _dailyPraiseList.asStateFlow()

    fun fetchDailyPraise() {
        viewModelScope.launch {
            _dailyPraiseList.value = UiState.Loading
            getDailyPraiseUseCase().onSuccess { domainList ->
                val uiList = domainList.map { it.toPresentation() }
                _dailyPraiseList.value = UiState.Success(uiList)
            }.onFailure { error ->
                _dailyPraiseList.value = UiState.Failure(error.message)
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