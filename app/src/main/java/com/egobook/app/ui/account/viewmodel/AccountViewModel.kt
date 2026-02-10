package com.egobook.app.ui.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _userIdState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val userIdState = _userIdState.asStateFlow()

    private val _linkState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val linkState = _linkState.asStateFlow()


    init {
        getUserId()
    }
    fun getUserId() {
        viewModelScope.launch {
            _userIdState.value = UiState.Loading

            accountRepository.getUserId()
                .onSuccess { id ->
                    _userIdState.value = UiState.Success(id)
                }
                .onFailure { e ->
                    _userIdState.value =
                        UiState.Failure(e.message ?: "사용자 ID를 불러오지 못했습니다")
                }
        }
    }

    fun linkToGoogle(idToken: String) {
        viewModelScope.launch {
            _linkState.value = UiState.Loading

            accountRepository.linkToGoogle(idToken)
                .onSuccess {
                    _linkState.value = UiState.Success(Unit)
                }
                .onFailure { e ->
                    _linkState.value =
                        UiState.Failure(e.message ?: "구글 계정 연동에 실패했습니다")
                }
        }

    }


}
