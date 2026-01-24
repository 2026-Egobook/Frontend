package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.DeleteFriendUseCase
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.GetIncomingFriendRequestsUseCase
import com.egobook.app.ui.square.model.FriendModel
import com.egobook.app.ui.square.model.FriendRequestModel
import com.egobook.app.ui.square.model.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendListUseCase: GetFriendListUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase,
    private val getIncomingFriendRequestsUseCase: GetIncomingFriendRequestsUseCase
): ViewModel() {

    private val _friendList = MutableStateFlow<UiState<List<FriendModel>>>(UiState.Idle)
    val friendList = _friendList.asStateFlow()

    fun fetchFriendList() {
        viewModelScope.launch {
            getFriendListUseCase().onSuccess { domainList ->
                _friendList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _friendList.value = UiState.Failure(error.message)
            }
        }
    }

    private val _deleteFriendResult = MutableSharedFlow<UiState<Long>>()
    val deleteFriendStatus = _deleteFriendResult.asSharedFlow()

    fun deleteFriend(deleteId: Long) {
        viewModelScope.launch {
            _deleteFriendResult.emit(UiState.Loading)
            deleteFriendUseCase(deleteId = deleteId).onSuccess {
                _deleteFriendResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _deleteFriendResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _incomingFriendRequestList = MutableStateFlow<UiState<List<FriendRequestModel>>>(UiState.Idle)
    val incomingFriendRequestList = _incomingFriendRequestList.asStateFlow()

    fun fetchIncomingFriendRequestList() {
        viewModelScope.launch {
            _incomingFriendRequestList.value = UiState.Loading
            getIncomingFriendRequestsUseCase().onSuccess { domainList ->
                _incomingFriendRequestList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _incomingFriendRequestList.value = UiState.Failure(error.message)
            }
        }
    }
}