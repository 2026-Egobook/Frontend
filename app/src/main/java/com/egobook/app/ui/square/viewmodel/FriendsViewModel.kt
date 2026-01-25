package com.egobook.app.ui.square.viewmodel

import androidx.compose.runtime.key
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.DeleteFriendUseCase
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.GetIncomingFriendRequestsUseCase
import com.egobook.app.domain.usecase.GetOutgoingFriendRequestsUseCase
import com.egobook.app.domain.usecase.RejectFriendRequestUseCase
import com.egobook.app.domain.usecase.RequestFriendshipUseCase
import com.egobook.app.domain.usecase.SearchUserUseCase
import com.egobook.app.ui.square.model.FriendModel
import com.egobook.app.ui.square.model.FriendRequestModel
import com.egobook.app.ui.square.model.SearchUserModel
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
    private val getIncomingFriendRequestsUseCase: GetIncomingFriendRequestsUseCase,
    private val getOutgoingFriendRequestsUseCase: GetOutgoingFriendRequestsUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val requestFriendshipUseCase: RequestFriendshipUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase
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

    private val _outgoingFriendRequestList = MutableStateFlow<UiState<List<FriendRequestModel>>>(UiState.Idle)
    val outgoingFriendRequestList = _outgoingFriendRequestList.asStateFlow()

    fun fetchOutgoingFriendRequestList() {
        viewModelScope.launch {
            _outgoingFriendRequestList.value = UiState.Loading
            getOutgoingFriendRequestsUseCase().onSuccess { domainList ->
                _outgoingFriendRequestList.value = UiState.Success(domainList.map { it.toPresentation() })
            }.onFailure { error ->
                _outgoingFriendRequestList.value = UiState.Failure(error.message)
            }
        }
    }

    private val _searchUserResult = MutableSharedFlow<UiState<SearchUserModel?>>()
    val searchUserResult = _searchUserResult.asSharedFlow()

    fun searchUser(keyword: String) {
        viewModelScope.launch {
            _searchUserResult.emit(UiState.Loading)
            searchUserUseCase(keyword = keyword).onSuccess { domainList ->
                _searchUserResult.emit(UiState.Success(domainList.map { it.toPresentation() }.firstOrNull()))
            }.onFailure { error ->
                _searchUserResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _requestFriendshipResult = MutableSharedFlow<UiState<Unit>>()
    val requestFriendshipResult = _requestFriendshipResult.asSharedFlow()

    fun requestFriendship(receiverId: Long) {
        viewModelScope.launch {
            _requestFriendshipResult.emit(UiState.Loading)
            requestFriendshipUseCase(receiverId = receiverId).onSuccess {
                _requestFriendshipResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _requestFriendshipResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _rejectFriendRequestResult = MutableSharedFlow<UiState<Long>>()
    val rejectFriendRequestResult = _rejectFriendRequestResult

    fun rejectFriendRequest(requestId: Long) {
        viewModelScope.launch {
            _rejectFriendRequestResult.emit(UiState.Loading)
            rejectFriendRequestUseCase(requestId = requestId).onSuccess { requestId ->
                _rejectFriendRequestResult.emit(UiState.Success(requestId))
            }.onFailure { error ->
                _rejectFriendRequestResult.emit(UiState.Failure(error.message))
            }
        }
    }

}