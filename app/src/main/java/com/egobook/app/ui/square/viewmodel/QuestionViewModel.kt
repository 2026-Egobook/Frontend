package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.GetTodayQuestionUseCase
import com.egobook.app.domain.usecase.SubmitTodayAnswerUseCase
import com.egobook.app.ui.square.model.question.TodayAnswerModel
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.model.question.toDomain
import com.egobook.app.ui.square.model.question.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val submitTodayAnswerUseCase: SubmitTodayAnswerUseCase
): ViewModel() {

    private val _todayQuestion = MutableStateFlow<UiState<TodayQuestionModel>>(UiState.Idle)
    val todayQuestion = _todayQuestion.asStateFlow()

    fun getTodayQuestion(isSubmit: Boolean) {
        viewModelScope.launch {
            _todayQuestion.value = UiState.Loading
            getTodayQuestionUseCase(isSubmit).onSuccess { question ->
                _todayQuestion.value = UiState.Success(question.toPresentation())
            }.onFailure { error ->
                _todayQuestion.value = UiState.Failure(error.message)
            }
        }
    }

    private val _submitTodayAnswerResult = MutableSharedFlow<UiState<Unit>>()
    val submitTodayAnswerResult = _submitTodayAnswerResult.asSharedFlow()

    fun submitTodayAnswer(answer: TodayAnswerModel) {
        viewModelScope.launch {
            _submitTodayAnswerResult.emit(UiState.Loading)
            submitTodayAnswerUseCase(answer = answer.toDomain()).onSuccess {
                _submitTodayAnswerResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _submitTodayAnswerResult.emit(UiState.Failure(error.message))
            }
        }
    }
}