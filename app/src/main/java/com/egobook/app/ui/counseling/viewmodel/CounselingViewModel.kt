package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.egobook.app.domain.usecase.egoroom.GetDailyAndWeeklyNotificationUseCase
import com.egobook.app.ui.counseling.model.DailyAndWeeklyNotificationModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounselingViewModel @Inject constructor(
    private val getDailyAndWeeklyNotificationUseCase: GetDailyAndWeeklyNotificationUseCase
): ViewModel() {

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
}

