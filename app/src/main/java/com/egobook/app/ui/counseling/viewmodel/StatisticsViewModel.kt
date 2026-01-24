package com.egobook.app.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.GetStatisticsUseCase
import com.egobook.app.ui.counseling.model.StatisticsModel
import com.egobook.app.ui.counseling.model.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val useCase: GetStatisticsUseCase): ViewModel() {

    private val _statisticsInfo = MutableStateFlow<UiState<StatisticsModel>>(UiState.Idle)
    val statisticsInfo = _statisticsInfo.asStateFlow()

    fun fetchStatistics() {
        viewModelScope.launch {
            _statisticsInfo.value = UiState.Loading
            useCase().onSuccess { domain ->
                _statisticsInfo.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _statisticsInfo.value = UiState.Failure(error.message)

            }
        }
    }
}