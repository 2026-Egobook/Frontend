package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.letter.DeferReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.DetectAbusiveContentUseCase
import com.egobook.app.domain.usecase.letter.GetArrivedPendingLetterUseCase
import com.egobook.app.domain.usecase.letter.ReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.SendLetterUseCase
import com.egobook.app.ui.square.model.friend.FriendModel
import com.egobook.app.ui.square.model.friend.toPresentation
import com.egobook.app.ui.square.model.letter.AbusiveContentModel
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterModel
import com.egobook.app.ui.square.model.letter.ReplyLetterModel
import com.egobook.app.ui.square.model.letter.SendLetterModel
import com.egobook.app.ui.square.model.letter.toDomain
import com.egobook.app.ui.square.model.letter.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterViewModel @Inject constructor(
    private val getFriendListUseCase: GetFriendListUseCase,
    private val sendLetterUseCase: SendLetterUseCase,
    private val detectAbusiveContentUseCase: DetectAbusiveContentUseCase,
    private val getArrivedPendingLetterUseCase: GetArrivedPendingLetterUseCase,
    private val replyLetterUseCase: ReplyLetterUseCase,
    private val deferReplyLetterUseCase: DeferReplyLetterUseCase
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
}