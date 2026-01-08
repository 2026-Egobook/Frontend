package com.example.egobook_frontent.ui.counseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.egobook_frontent.domain.usecase.GetWeeklyReportUseCase
import com.example.egobook_frontent.ui.counseling.model.WeeklyReportModel
import com.example.egobook_frontent.ui.counseling.model.toPresentation
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeeklyReportViewModel @Inject constructor(private val useCase: GetWeeklyReportUseCase): ViewModel() {

    private val _weeklyReportList = MutableStateFlow<UiState<List<WeeklyReportModel>>>(UiState.Idle)
    val weeklyReportList = _weeklyReportList.asStateFlow()

    fun fetchWeeklyReport() {
        viewModelScope.launch {
            _weeklyReportList.value = UiState.Loading
            useCase().onSuccess { domainList ->
                _weeklyReportList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _weeklyReportList.value = UiState.Failure(error.message)
            }
        }
    }
}