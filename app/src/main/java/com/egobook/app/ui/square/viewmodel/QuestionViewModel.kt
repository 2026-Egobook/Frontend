package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.GetTodayQuestionUseCase
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.model.question.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(private val getTodayQuestionUseCase: GetTodayQuestionUseCase): ViewModel() {

    private val _todayQuestion = MutableStateFlow<UiState<TodayQuestionModel>>(UiState.Idle)
    val todayQuestion = _todayQuestion.asStateFlow()

    fun getTodayQuestion() {
        viewModelScope.launch {
            _todayQuestion.value = UiState.Loading
            getTodayQuestionUseCase().onSuccess { question ->
                _todayQuestion.value = UiState.Success(question.toPresentation())
            }.onFailure { error ->
                _todayQuestion.value = UiState.Failure(error.message)
            }
        }
    }
}