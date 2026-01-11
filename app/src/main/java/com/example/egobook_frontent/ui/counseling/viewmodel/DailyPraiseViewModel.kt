package com.example.egobook_frontent.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.egobook_frontent.domain.usecase.GetDailyPraiseUseCase
import com.example.egobook_frontent.domain.usecase.GetNotificationUseCase
import com.example.egobook_frontent.domain.usecase.UpdateNotificationUseCase
import com.example.egobook_frontent.ui.counseling.model.PraiseMessageModel
import com.example.egobook_frontent.ui.counseling.model.toPresentation
import com.example.egobook_frontent.ui.notification.model.NotificationModel
import com.example.egobook_frontent.ui.notification.model.toPresentation
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyPraiseViewModel @Inject constructor(
    private val getDailyPraiseUseCase: GetDailyPraiseUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase
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

    private val _notificationStatus = MutableStateFlow<UiState<NotificationModel>>(UiState.Idle)
    val notificationStatus = _notificationStatus.asStateFlow()

    fun fetchNotificationStatus() {
        viewModelScope.launch {
            _notificationStatus.value = UiState.Loading
            getNotificationUseCase().onSuccess { domain ->
                _notificationStatus.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _notificationStatus.value = UiState.Failure(error.message)
            }
        }
    }

    private val _updateNotificationResult = MutableSharedFlow<UiState<Boolean>>()
    val updateNotificationResult = _updateNotificationResult.asSharedFlow()

    fun updateNotificationStatus(isEnabled: Boolean) {
        viewModelScope.launch {
            _updateNotificationResult.emit(UiState.Loading)
            updateNotificationUseCase(isEnabled = isEnabled).onSuccess { updateStatus ->
                _updateNotificationResult.emit(UiState.Success(updateStatus))
            }.onFailure { error ->
                _updateNotificationResult.emit(UiState.Failure(error.message))
            }
        }
    }

}