package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.usecase.AcceptFriendRequestUseCase
import com.egobook.app.domain.usecase.CancelFriendRequestUseCase
import com.egobook.app.domain.usecase.DeleteFriendUseCase
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.GetIncomingFriendRequestsUseCase
import com.egobook.app.domain.usecase.GetOutgoingFriendRequestsUseCase
import com.egobook.app.domain.usecase.GetUserIdUseCase
import com.egobook.app.domain.usecase.RejectFriendRequestUseCase
import com.egobook.app.domain.usecase.RequestFriendshipUseCase
import com.egobook.app.domain.usecase.SearchUserUseCase
import com.egobook.app.ui.square.model.friend.FriendListModel
import com.egobook.app.ui.square.model.friend.FriendRequestModel
import com.egobook.app.ui.square.model.friend.SearchUserModel
import com.egobook.app.ui.square.model.friend.toPresentation
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
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {

    private val _friendList = MutableStateFlow<UiState<FriendListModel>>(UiState.Idle)
    val friendList = _friendList.asStateFlow()

    fun fetchFriendList() {
        viewModelScope.launch {
            getFriendListUseCase().onSuccess { domainList ->
                _friendList.value = UiState.Success(domainList.toPresentation())
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

    private val _searchUserResult = MutableSharedFlow<UiState<List<SearchUserModel>?>>()
    val searchUserResult = _searchUserResult.asSharedFlow()

    fun searchUser(keyword: String) {
        viewModelScope.launch {
            _searchUserResult.emit(UiState.Loading)
            searchUserUseCase(keyword = keyword).onSuccess { domainList ->
                _searchUserResult.emit(UiState.Success(domainList?.map { it.toPresentation() }))
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

    private val _acceptFriendRequestResult = MutableSharedFlow<UiState<Long>>()
    val acceptFriendRequestResult = _acceptFriendRequestResult

    fun acceptFriendRequest(requestId: Long) {
        viewModelScope.launch {
            _acceptFriendRequestResult.emit(UiState.Loading)
            acceptFriendRequestUseCase(requestId = requestId).onSuccess { requestId ->
                _acceptFriendRequestResult.emit(UiState.Success(requestId))
            }.onFailure { error ->
                _acceptFriendRequestResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _cancelFriendRequestResult = MutableSharedFlow<UiState<Long>>()
    val cancelFriendRequestResult = _cancelFriendRequestResult.asSharedFlow()

    fun cancelFriendRequest(requestId: Long) {
        viewModelScope.launch {
            _cancelFriendRequestResult.emit(UiState.Loading)
            cancelFriendRequestUseCase(requestId = requestId).onSuccess { requestId ->
                _cancelFriendRequestResult.emit(UiState.Success(requestId))
            }.onFailure { error ->
                _cancelFriendRequestResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _userId = MutableStateFlow<UiState<String>>(UiState.Idle)
    val userId = _userId.asSharedFlow()

    fun getUserId() {
        viewModelScope.launch {
            _userId.emit(UiState.Loading)
            getUserIdUseCase().onSuccess { userId ->
                _userId.emit( UiState.Success(userId))
            }.onFailure { error ->
                _userId.emit(UiState.Failure(error.message))
            }
        }
    }

}