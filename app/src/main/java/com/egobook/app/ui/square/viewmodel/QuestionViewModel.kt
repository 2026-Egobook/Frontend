package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.domain.usecase.GetMyRepliesHistoryUseCase
import com.egobook.app.domain.usecase.GetTodayAllUserRepliesUseCase
import com.egobook.app.domain.usecase.GetTodayFriendsRepliesUseCase
import com.egobook.app.domain.usecase.GetTodayQuestionUseCase
import com.egobook.app.domain.usecase.SubmitTodayAnswerUseCase
import com.egobook.app.domain.usecase.UpdateTodayAnswerUseCase
import com.egobook.app.ui.square.model.question.MyTodayQuestionAnswerItemModel
import com.egobook.app.ui.square.model.question.TodayAnswerModel
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.model.question.UserTodayQuestionAnswerItemModel
import com.egobook.app.ui.square.model.question.toDomain
import com.egobook.app.ui.square.model.question.toPresentation
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
class QuestionViewModel @Inject constructor(
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val submitTodayAnswerUseCase: SubmitTodayAnswerUseCase,
    private val getMyRepliesHistoryUseCase: GetMyRepliesHistoryUseCase,
    private val getTodayFriendsRepliesUseCase: GetTodayFriendsRepliesUseCase,
    private val getTodayAllUserRepliesUseCase: GetTodayAllUserRepliesUseCase,
    private val updateTodayAnswerUseCase: UpdateTodayAnswerUseCase
): ViewModel() {

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

    private val _myRepliesHistory = MutableStateFlow<PagingData<MyTodayQuestionAnswerItemModel>?>(null)
    val myRepliesHistory = _myRepliesHistory.asStateFlow()

    fun getMyRepliesHistory(size: Int) {
        viewModelScope.launch {
            getMyRepliesHistoryUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _myRepliesHistory.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _todayFriendsReplies = MutableStateFlow<PagingData<UserTodayQuestionAnswerItemModel>?>(null)
    val todayFriendsReplies = _todayFriendsReplies.asStateFlow()

    fun getTodayFriendsReplies(size: Int) {
        viewModelScope.launch {
            getTodayFriendsRepliesUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _todayFriendsReplies.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _todayAllUserReplies = MutableStateFlow<PagingData<UserTodayQuestionAnswerItemModel>?>(null)
    val todayAllUserReplies = _todayAllUserReplies.asStateFlow()

    fun getTodayAllUserReplies(size: Int) {
        viewModelScope.launch {
            getTodayAllUserRepliesUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _todayAllUserReplies.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _updateTodayAnswerResult = MutableSharedFlow<UiState<Unit>>()
    val updateTodayAnswerResult = _updateTodayAnswerResult.asSharedFlow()

    fun updateTodayAnswer(updatedAnswer: TodayAnswerModel) {
        viewModelScope.launch {
            _updateTodayAnswerResult.emit(UiState.Loading)
            updateTodayAnswerUseCase(updatedAnswer = updatedAnswer.toDomain()).onSuccess {
                _updateTodayAnswerResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _updateTodayAnswerResult.emit(UiState.Failure(error.message))
            }
        }
    }
}