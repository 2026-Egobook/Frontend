package com.example.egobook_frontent.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.egobook_frontent.domain.usecase.GetStatisticsUseCase
import com.example.egobook_frontent.ui.counseling.model.StatisticsModel
import com.example.egobook_frontent.ui.counseling.model.toPresentation
import com.example.egobook_frontent.util.UiState
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