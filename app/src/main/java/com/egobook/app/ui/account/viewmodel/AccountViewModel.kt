package com.egobook.app.ui.account.viewmodel

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userInfoStorage: UserInfoStorage
) : ViewModel() {
    private val _userIdState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val userIdState = _userIdState.asStateFlow()

    private val _linkState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val linkState = _linkState.asStateFlow()


    init {
        //로그인 타입을 확인하여 연동 가능 여부를 판단
        viewModelScope.launch {
            val loginType = userInfoStorage.getLoginType().firstOrNull()
            if (loginType == UserInfoStorage.LoginType.GOOGLE) {
                _linkState.value = UiState.Success(Unit)
            }
        }

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

                    // 연동 성공 후 userId 갱신
                    accountRepository.getUserId(forceRefresh = true)
                        .onSuccess { id ->
                            _userIdState.value = UiState.Success(id)
                        }
                        .onFailure { e ->
                            _userIdState.value =
                                UiState.Failure(e.message ?: "사용자 ID를 새로 불러오지 못했습니다")
                        }
                }
                .onFailure { e ->
                    _linkState.value =
                        UiState.Failure(e.message ?: "구글 계정 연동에 실패했습니다")
                }
        }

    }

}
