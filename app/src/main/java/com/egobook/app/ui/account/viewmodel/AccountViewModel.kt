package com.egobook.app.ui.account.viewmodel

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _linkToastEvent = MutableSharedFlow<String>(replay = 1)
    val linkToastEvent = _linkToastEvent.asSharedFlow()

    // UserInfoStorage에서 직접 이메일을 읽어옴
    val userEmail = userInfoStorage.getUserEmail()

    init {
        //로그인 타입을 확인하여 연동 상태 초기화
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
                    _linkToastEvent.emit("Google 계정 연동이 완료되었습니다!")

                    // 연동 성공 후 userId 갱신
                    accountRepository.getUserId(forceRefresh = true)
                        .onSuccess { id ->
                            _userIdState.value = UiState.Success(id)
                        }
                        .onFailure { e ->
                            _userIdState.value =
                                UiState.Failure(e.message ?: "사용자 ID를 새로 불러오지 못했습니다")
                            _linkToastEvent.emit(e.message ?: "구글 계정 연동 실패")
                        }
                }
                .onFailure { e ->
                    _linkState.value =
                        UiState.Failure(e.message ?: "구글 계정 연동에 실패했습니다")
                    _linkToastEvent.emit("구글 계정 연동에 실패했습니다.")
                }
        }

    }

}
