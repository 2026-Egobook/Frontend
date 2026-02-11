package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.letter.DeferReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.DeleteLetterThreadUseCase
import com.egobook.app.domain.usecase.letter.DetectAbusiveContentUseCase
import com.egobook.app.domain.usecase.letter.GetArrivedPendingLetterUseCase
import com.egobook.app.domain.usecase.letter.GetSentLetterWithReplyUseCase
import com.egobook.app.domain.usecase.letter.GetSentLettersUseCase
import com.egobook.app.domain.usecase.letter.GiveUpReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.ReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.ReportRepliedLetterUseCase
import com.egobook.app.domain.usecase.letter.SendLetterUseCase
import com.egobook.app.ui.square.model.friend.FriendModel
import com.egobook.app.ui.square.model.friend.toPresentation
import com.egobook.app.ui.square.model.letter.AbusiveContentModel
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterModel
import com.egobook.app.ui.square.model.letter.ReplyLetterModel
import com.egobook.app.ui.square.model.letter.ReportLetterModel
import com.egobook.app.ui.square.model.letter.SendLetterModel
import com.egobook.app.ui.square.model.letter.SentLetterModel
import com.egobook.app.ui.square.model.letter.SentLetterWithReplyModel
import com.egobook.app.ui.square.model.letter.toDomain
import com.egobook.app.ui.square.model.letter.toPresentation
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
class LetterViewModel @Inject constructor(
    private val getFriendListUseCase: GetFriendListUseCase,
    private val sendLetterUseCase: SendLetterUseCase,
    private val detectAbusiveContentUseCase: DetectAbusiveContentUseCase,
    private val getArrivedPendingLetterUseCase: GetArrivedPendingLetterUseCase,
    private val replyLetterUseCase: ReplyLetterUseCase,
    private val deferReplyLetterUseCase: DeferReplyLetterUseCase,
    private val giveUpReplyLetterUseCase: GiveUpReplyLetterUseCase,
    private val getSentLettersUseCase: GetSentLettersUseCase,
    private val getSentLetterWithReplyUseCase: GetSentLetterWithReplyUseCase,
    private val reportRepliedLetterUseCase: ReportRepliedLetterUseCase,
    private val deleteLetterThreadUseCase: DeleteLetterThreadUseCase
): ViewModel() {

    private val _friendList = MutableStateFlow<UiState<List<FriendModel>>>(UiState.Idle)
    val friendList = _friendList.asStateFlow()

    fun getFriendList() {
        viewModelScope.launch {
            _friendList.value = UiState.Loading
            getFriendListUseCase().onSuccess { domainList ->
                _friendList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _friendList.value = UiState.Failure(error.message)
            }
        }
    }

    private val _sendLetterResult = MutableSharedFlow<UiState<Unit>>()
    val sendLetterResult = _sendLetterResult.asSharedFlow()

    fun sendLetter(letter: SendLetterModel) {
        viewModelScope.launch {
            _sendLetterResult.emit(UiState.Loading)
            sendLetterUseCase(letter = letter.toDomain()).onSuccess {
                _sendLetterResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _sendLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _detectAbusiveContentResult = MutableSharedFlow<UiState<AbusiveContentModel>>()
    val detectAbusiveContentResult = _detectAbusiveContentResult.asSharedFlow()

    fun detectAbusiveContent(text: String) {
        viewModelScope.launch {
            _detectAbusiveContentResult.emit(UiState.Loading)
            detectAbusiveContentUseCase(text = text).onSuccess { domain ->
                _detectAbusiveContentResult.emit(UiState.Success(domain.toPresentation()))
            }.onFailure { error ->
                _detectAbusiveContentResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _arrivedPendingLetterResult = MutableStateFlow<UiState<ArrivedPendingLetterModel>>(UiState.Idle) // stateflow vs sharedflow
    val arrivedPendingLetterResult = _arrivedPendingLetterResult.asStateFlow()

    fun getArrivedPendingLetter() {
        viewModelScope.launch {
            _arrivedPendingLetterResult.value = UiState.Loading
            getArrivedPendingLetterUseCase().onSuccess { domain ->
                _arrivedPendingLetterResult.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _arrivedPendingLetterResult.value = UiState.Failure(error.message)
            }
        }
    }

    private val _replyLetterResult = MutableSharedFlow<UiState<ReplyLetterModel>>()
    val replyLetterResult = _replyLetterResult.asSharedFlow()

    fun replyLetter(letterId: Long, text: String) {
        viewModelScope.launch {
            _replyLetterResult.emit(UiState.Loading)
            replyLetterUseCase(letterId = letterId, text = text).onSuccess { domain ->
                _replyLetterResult.emit(UiState.Success(domain.toPresentation()))
            }.onFailure { error ->
                _replyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deferReplyLetterResult = MutableSharedFlow<UiState<Unit>>()
    val deferReplyLetterResult = _deferReplyLetterResult.asSharedFlow()

    fun deferReplyLetter(letterId: Long) {
        viewModelScope.launch {
            _deferReplyLetterResult.emit(UiState.Loading)
            deferReplyLetterUseCase(letterId = letterId).onSuccess {
                _deferReplyLetterResult.emit(UiState.Success(Unit))
            }.onFailure { error ->
                _deferReplyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _giveUpReplyLetterResult = MutableSharedFlow<UiState<Unit>>()
    val giveUpReplyLetterResult = _giveUpReplyLetterResult.asSharedFlow()

    fun giveUpReplyLetter(letterId: Long) {
        viewModelScope.launch {
            _giveUpReplyLetterResult.emit(UiState.Loading)
            giveUpReplyLetterUseCase(letterId = letterId).onSuccess {
                _giveUpReplyLetterResult.emit(UiState.Success(Unit))
            }.onFailure { error ->
                _giveUpReplyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _sentLetters = MutableStateFlow<PagingData<SentLetterModel>?>(null)
    val sentLetters = _sentLetters.asStateFlow()

    fun getSentLetters(size: Int) {
        viewModelScope.launch {
            getSentLettersUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _sentLetters.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _sentLetterWithReply = MutableStateFlow<UiState<SentLetterWithReplyModel>>(UiState.Idle)
    val sentLetterWithReply = _sentLetterWithReply.asStateFlow()

    fun getSentLetterWithReply(letterId: Long) {
        viewModelScope.launch {
            _sentLetterWithReply.value = UiState.Loading
            getSentLetterWithReplyUseCase(letterId = letterId).onSuccess { domain ->
                _sentLetterWithReply.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _sentLetterWithReply.value = UiState.Failure(error.message)
            }
        }
    }

    private val _reportRepliedLetterResult = MutableSharedFlow<UiState<Unit>>()
    val reportRepliedLetterResult = _reportRepliedLetterResult.asSharedFlow()

    fun reportRepliedLetter(replyId: Long, reportLetter: ReportLetterModel) {
        viewModelScope.launch {
            _reportRepliedLetterResult.emit(UiState.Loading)
            reportRepliedLetterUseCase(replyId = replyId, reportLetter = reportLetter.toDomain()).onSuccess {
                _reportRepliedLetterResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _reportRepliedLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deleteLetterThreadResult = MutableSharedFlow<UiState<Unit>>()
    val deleteLetterThreadResult = _deleteLetterThreadResult.asSharedFlow()

    fun deleteLetterThread(threadId: Long) {
        viewModelScope.launch {
            _deleteLetterThreadResult.emit(UiState.Loading)
            deleteLetterThreadUseCase(threadId = threadId).onSuccess {
                _deleteLetterThreadResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _deleteLetterThreadResult.emit(UiState.Failure(error.message))
            }
        }
    }
}