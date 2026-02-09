package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.usecase.egoroom.GetDailyPraiseByDateUseCase
import com.egobook.app.domain.usecase.egoroom.GetDailyPraiseUseCase
import com.egobook.app.ui.counseling.model.DailyPraiseDetailModel
import com.egobook.app.ui.counseling.model.PraiseDailyModel
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyPraiseViewModel @Inject constructor(
    private val getDailyPraiseUseCase: GetDailyPraiseUseCase,
    private val getDailyPraiseByDateUseCase: GetDailyPraiseByDateUseCase,
    private val notificationDelegate: NotificationDelegate
): ViewModel() {

    private val _dailyPraiseList = MutableStateFlow<PagingData<PraiseDailyModel>>(PagingData.empty())
    val dailyPraiseList = _dailyPraiseList.asStateFlow()

    fun fetchDailyPraises(size: Int) {
        viewModelScope.launch {
            getDailyPraiseUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _dailyPraiseList.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _dailyPraiseByDate = MutableSharedFlow<UiState<DailyPraiseDetailModel>>()
    val dailyPraiseByDate = _dailyPraiseByDate.asSharedFlow()

    fun fetchDailyPraiseByData(date: String) {
        viewModelScope.launch {
            _dailyPraiseByDate.emit(UiState.Loading)
            getDailyPraiseByDateUseCase(date = date).onSuccess { domain ->
                _dailyPraiseByDate.emit(UiState.Success(domain.toPresentation()))
            }.onFailure { error ->
                _dailyPraiseByDate.emit(UiState.Failure(error.message))
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