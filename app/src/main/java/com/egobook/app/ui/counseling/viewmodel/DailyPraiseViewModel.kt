package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.domain.usecase.egoroom.GetDailyAndWeeklyNotificationUseCase
import com.egobook.app.domain.usecase.egoroom.GetDailyPraiseByDateUseCase
import com.egobook.app.domain.usecase.egoroom.GetDailyPraiseUseCase
import com.egobook.app.domain.usecase.egoroom.UpdateDailyPraiseNotificationUseCase
import com.egobook.app.ui.counseling.model.DailyAndWeeklyNotificationModel
import com.egobook.app.ui.counseling.model.DailyPraiseDetailModel
import com.egobook.app.ui.counseling.model.PraiseDailyModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyPraiseViewModel @Inject constructor(
    private val getDailyPraiseUseCase: GetDailyPraiseUseCase,
    private val getDailyPraiseByDateUseCase: GetDailyPraiseByDateUseCase,
    private val getDailyAndWeeklyNotificationUseCase: GetDailyAndWeeklyNotificationUseCase,
    private val updateDailyPraiseNotificationUseCase: UpdateDailyPraiseNotificationUseCase
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

    private val _dailyAndWeeklyNotification = MutableStateFlow<UiState<DailyAndWeeklyNotificationModel>>(UiState.Idle)
    val dailyAndWeeklyNotification = _dailyAndWeeklyNotification.asStateFlow()

    fun getDailyAndWeeklyNotification() {
        viewModelScope.launch {
            _dailyAndWeeklyNotification.value = UiState.Loading
            getDailyAndWeeklyNotificationUseCase().onSuccess { domain ->
                _dailyAndWeeklyNotification.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _dailyAndWeeklyNotification.value = UiState.Failure(error.message)
            }
        }
    }

    private val _updateNotificationStatus = MutableSharedFlow<UiState<Boolean>>()
    val updateNotificationStatus = _updateNotificationStatus.asSharedFlow()

    fun updateDailyPraiseNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            _updateNotificationStatus.emit(UiState.Loading)
            updateDailyPraiseNotificationUseCase(isEnabled = isEnabled).onSuccess { isEnabled ->
                _updateNotificationStatus.emit(UiState.Success(isEnabled))
            }.onFailure { error ->
                _updateNotificationStatus.emit(UiState.Failure(error.message))
            }
        }
    }

}