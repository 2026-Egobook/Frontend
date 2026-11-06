package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
import com.egobook.app.domain.usecase.DeleteMyQuestionAnswerUseCase
import com.egobook.app.domain.usecase.GetMyRepliesHistoryUseCase
import com.egobook.app.domain.usecase.GetTodayAllUserRepliesUseCase
import com.egobook.app.domain.usecase.GetTodayFriendsRepliesUseCase
import com.egobook.app.domain.usecase.GetTodayQuestionUseCase
import com.egobook.app.domain.usecase.SubmitTodayAnswerUseCase
import com.egobook.app.domain.usecase.UpdateMarketingConsentUseCase
import com.egobook.app.domain.usecase.UpdateTodayAnswerUseCase
import com.egobook.app.domain.usecase.question.ReportTodayQuestionAnswerUseCase
import com.egobook.app.domain.usecase.question.ToggleAnswerEmpathyUseCase
import com.egobook.app.ui.square.model.letter.ReportContentModel
import com.egobook.app.ui.square.model.letter.toDomain
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val submitTodayAnswerUseCase: SubmitTodayAnswerUseCase,
    private val getMyRepliesHistoryUseCase: GetMyRepliesHistoryUseCase,
    private val getTodayFriendsRepliesUseCase: GetTodayFriendsRepliesUseCase,
    private val getTodayAllUserRepliesUseCase: GetTodayAllUserRepliesUseCase,
    private val updateTodayAnswerUseCase: UpdateTodayAnswerUseCase,
    private val deleteMyQuestionAnswerUseCase: DeleteMyQuestionAnswerUseCase,
    private val reportTodayQuestionAnswerUseCase: ReportTodayQuestionAnswerUseCase,
    private val updateMarketingConsentUseCase: UpdateMarketingConsentUseCase,
    private val toggleAnswerEmpathyUseCase: ToggleAnswerEmpathyUseCase,
    private val analyticsLogger: AnalyticsLogger
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
                analyticsLogger.logEvent(
                    AnalyticsEvent.QUESTION_ANSWER_WRITE,
                    mapOf(AnalyticsParam.VISIBILITY to answer.visibilityType.value.lowercase())
                )
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

    /** 공감 토글 결과를 페이징 재조회 없이 목록에 반영하기 위한 answerId별 상태 */
    private val empathyOverrides = MutableStateFlow<Map<Long, AnswerEmpathyState>>(emptyMap())
    private val empathyInProgressIds = mutableSetOf<Long>()

    fun getTodayAllUserReplies(size: Int) {
        empathyOverrides.value = emptyMap()
        viewModelScope.launch {
            analyticsLogger.logEvent(AnalyticsEvent.QUESTION_VIEW_ALL)
            getTodayAllUserRepliesUseCase(size = size)
                .cachedIn(viewModelScope)
                .combine(empathyOverrides) { pagingData, overrides ->
                    pagingData.map { item ->
                        val model = item.toPresentation()
                        overrides[model.answerId]?.let {
                            model.copy(empathized = it.empathized, empathyCount = it.empathyCount)
                        } ?: model
                    }
                }
                .collectLatest { pagingData ->
                    _todayAllUserReplies.value = pagingData
                }
        }
    }

    private val _toggleAnswerEmpathyError = MutableSharedFlow<String?>()
    val toggleAnswerEmpathyError = _toggleAnswerEmpathyError.asSharedFlow()

    fun toggleAnswerEmpathy(item: UserTodayQuestionAnswerItemModel) {
        val answerId = item.answerId
        if (!empathyInProgressIds.add(answerId)) return

        val previous = AnswerEmpathyState(empathized = item.empathized, empathyCount = item.empathyCount)
        val optimistic = AnswerEmpathyState(
            empathized = !previous.empathized,
            empathyCount = if (previous.empathized) {
                (previous.empathyCount - 1).coerceAtLeast(0)
            } else {
                previous.empathyCount + 1
            }
        )
        empathyOverrides.update { it + (answerId to optimistic) }

        viewModelScope.launch {
            toggleAnswerEmpathyUseCase(answerId = answerId).onSuccess { result ->
                empathyOverrides.update {
                    it + (answerId to AnswerEmpathyState(result.empathized, result.empathyCount))
                }
            }.onFailure { error ->
                empathyOverrides.update { it + (answerId to previous) }
                _toggleAnswerEmpathyError.emit(error.message)
            }
            empathyInProgressIds.remove(answerId)
        }
    }

    private data class AnswerEmpathyState(
        val empathized: Boolean,
        val empathyCount: Long
    )

    private val _updateTodayAnswerResult = MutableSharedFlow<UiState<Unit>>()
    val updateTodayAnswerResult = _updateTodayAnswerResult.asSharedFlow()

    fun updateTodayAnswer(updatedAnswer: TodayAnswerModel) {
        viewModelScope.launch {
            _updateTodayAnswerResult.emit(UiState.Loading)
            updateTodayAnswerUseCase(updatedAnswer = updatedAnswer.toDomain()).onSuccess {
                analyticsLogger.logEvent(AnalyticsEvent.QUESTION_ANSWER_EDIT)
                _updateTodayAnswerResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _updateTodayAnswerResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deleteMyQuestionAnswerResult = MutableSharedFlow<UiState<Unit>>()
    val deleteMyQuestionAnswerResult = _deleteMyQuestionAnswerResult

    fun deleteMyQuestionAnswer(answerId: Long) {
        viewModelScope.launch {
            _deleteMyQuestionAnswerResult.emit(UiState.Loading)
            deleteMyQuestionAnswerUseCase(answerId = answerId).onSuccess {
                analyticsLogger.logEvent(AnalyticsEvent.QUESTION_ANSWER_DELETE)
                _deleteMyQuestionAnswerResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _deleteMyQuestionAnswerResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _reportTodayQuestionAnswerResult = MutableSharedFlow<UiState<Unit>>()
    val reportTodayQuestionAnswerResult = _reportTodayQuestionAnswerResult.asSharedFlow()

    fun reportTodayQuestionAnswer(answerId: Long, request: ReportContentModel) {
        viewModelScope.launch {
            _reportTodayQuestionAnswerResult.emit(UiState.Loading)
            reportTodayQuestionAnswerUseCase(answerId = answerId, request = request.toDomain()).onSuccess {
                analyticsLogger.logEvent(
                    AnalyticsEvent.QUESTION_ANSWER_REPORT,
                    mapOf(AnalyticsParam.REASON to request.reason.value.lowercase())
                )
                _reportTodayQuestionAnswerResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _reportTodayQuestionAnswerResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _updateMarketingConsentResult = MutableSharedFlow<UiState<Boolean>>()
    val updateMarketingConsentResult = _updateMarketingConsentResult.asSharedFlow()

    fun updateMarketingConsent(enabled: Boolean) {
        viewModelScope.launch {
            _updateMarketingConsentResult.emit(UiState.Loading)
            updateMarketingConsentUseCase(enabled = enabled).onSuccess {
                _updateMarketingConsentResult.emit(UiState.Success(enabled))
            }.onFailure { error ->
                _updateMarketingConsentResult.emit(UiState.Failure(error.message))
            }
        }
    }
}