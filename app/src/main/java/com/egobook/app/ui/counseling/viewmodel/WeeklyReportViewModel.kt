package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.domain.usecase.GetUserInfoUseCase
import com.egobook.app.domain.usecase.egoroom.GetWeeklyReportStyleUseCase
import com.egobook.app.domain.usecase.GetWeeklyReportsUseCase
import com.egobook.app.domain.usecase.egoroom.UpdateWeeklyReportStyleUseCase
import com.egobook.app.domain.usecase.egoroom.GetDailyAndWeeklyNotificationUseCase
 import com.egobook.app.domain.usecase.egoroom.GetWeeklyReportByDateUseCase
import com.egobook.app.domain.usecase.egoroom.UnlockWeeklyReportUseCase
import com.egobook.app.domain.usecase.egoroom.UpdateWeeklyReportNotificationUseCase
import com.egobook.app.ui.counseling.model.DailyAndWeeklyNotificationModel
import com.egobook.app.ui.counseling.model.WeeklyReportDetailModel
import com.egobook.app.ui.counseling.model.WeeklyReportModel
import com.egobook.app.ui.counseling.model.WeeklyReportStyleModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.ui.home.user.User
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
class WeeklyReportViewModel @Inject constructor(
    private val getWeeklyReportsUseCase: GetWeeklyReportsUseCase,
    private val getWeeklyReportStyleUseCase: GetWeeklyReportStyleUseCase,
    private val getDailyAndWeeklyNotificationUseCase: GetDailyAndWeeklyNotificationUseCase,
    private val updateWeeklyReportStyleUseCase: UpdateWeeklyReportStyleUseCase,
    private val updateWeeklyReportNotificationUseCase: UpdateWeeklyReportNotificationUseCase,
    private val getWeeklyReportByDateUseCase: GetWeeklyReportByDateUseCase,
    private val unlockWeeklyReportUseCase: UnlockWeeklyReportUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
): ViewModel() {

    private val _weeklyReportList = MutableStateFlow<PagingData<WeeklyReportModel>>(PagingData.empty())
    val weeklyReportList = _weeklyReportList.asStateFlow()

    fun fetchWeeklyReport(size: Int) {
        viewModelScope.launch {
            getWeeklyReportsUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _weeklyReportList.value = pagingData.map { it.toPresentation() }
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

    fun updateWeeklyReportNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            updateWeeklyReportNotificationUseCase(isEnabled = isEnabled).onSuccess { isEnabled ->
                _updateNotificationStatus.emit(UiState.Success(isEnabled))
            }.onFailure { error ->
                _updateNotificationStatus.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _weeklyReportByDate = MutableStateFlow<UiState<WeeklyReportDetailModel>>(UiState.Idle)
    val weeklyReportByDate = _weeklyReportByDate.asStateFlow()

    fun getWeeklyReportByDate(startDate: String) {
        viewModelScope.launch {
            _weeklyReportByDate.value = UiState.Loading
            getWeeklyReportByDateUseCase(startDate = startDate).onSuccess { domain ->
                _weeklyReportByDate.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _weeklyReportByDate.value = UiState.Failure(error.message)
            }
        }
    }

    private val _weeklyReportStyle = MutableStateFlow<UiState<ReportStyle>>(UiState.Idle)
    val weeklyReportStyle = _weeklyReportStyle.asStateFlow()

    fun fetchWeeklyReportStyle() {
        viewModelScope.launch {
            getWeeklyReportStyleUseCase().onSuccess { reportStyle ->
                _weeklyReportStyle.value = UiState.Success(reportStyle)
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

    private val _unlockWeeklyReportResult = MutableSharedFlow<UiState<Unit>>()
    val unlockWeeklyReportResult = _unlockWeeklyReportResult.asSharedFlow()

    fun unlockWeeklyReport(startDate: String, unlockType: WeeklyReportUnlockType) {
        viewModelScope.launch {
            _unlockWeeklyReportResult.emit(UiState.Loading)
            unlockWeeklyReportUseCase(startDate = startDate, unlockType = unlockType).onSuccess {
                _unlockWeeklyReportResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _unlockWeeklyReportResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _userInfo = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userInfo = _userInfo.asStateFlow()

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfo.value = UiState.Loading
            getUserInfoUseCase().onSuccess { domain ->
                _userInfo.value = UiState.Success(domain)
            }.onFailure { error ->
                _userInfo.value = UiState.Failure(error.message)
            }
        }
    }
}