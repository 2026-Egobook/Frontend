package com.egobook.app.ui.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.domain.model.account.NicknameValidationResult
import com.egobook.app.domain.model.account.NicknameValidator
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.egobook.app.domain.model.auth.AuthError

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
) : ViewModel() {
        private val _userIdState = MutableStateFlow<UiState<String>>(UiState.Idle)
        val userIdState = _userIdState.asStateFlow()

    private val _linkState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val linkState = _linkState.asStateFlow()

    private val _linkToastEvent = MutableSharedFlow<String>(replay = 0)
    val linkToastEvent = _linkToastEvent.asSharedFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteAccountState = _deleteAccountState.asStateFlow()

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname = _nickname.asStateFlow()

    private val _nicknameUpdateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val nicknameUpdateState = _nicknameUpdateState.asStateFlow()

    private val _nicknameToastEvent = MutableSharedFlow<String>(replay = 0)
    val nicknameToastEvent = _nicknameToastEvent.asSharedFlow()

    init {
        loadLinkedAccountInfo()
        getUserId()
        loadNickname()
    }

    private fun loadLinkedAccountInfo() {
        viewModelScope.launch {
            accountRepository.getLinkedAccountInfo()
                .onSuccess { info ->
                    if (info.isGoogleLinked) {
                        _userEmail.value = info.email
                        _linkState.value = UiState.Success(Unit)
                    }
                }
        }
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
                    val errorMessage = when (e) {
                        is AuthError.BadRequest -> "잘못된 요청입니다. 다시 시도해주세요."
                        is AuthError.InvalidCredentials -> "GUEST 로그인이 되어 있지 않습니다. 다시 로그인해주세요."
                        is AuthError.UserNotFound -> "Guest 계정 정보를 찾을 수 없습니다."
                        is AuthError.UserAlreadyExists -> "이미 연동된 Google 계정입니다."
                        is AuthError.NetworkError -> "네트워크 연결을 확인해주세요."
                        else -> e.message ?: "구글 계정 연동에 실패했습니다. 잠시 후 다시 시도해주세요"
                    }
                    _linkState.value = UiState.Failure(errorMessage)
                    _linkToastEvent.emit(errorMessage)
                }
        }
    }

    private fun loadNickname() {
        viewModelScope.launch {
            try {
                _nickname.value = userRepository.load().nickname
            } catch (e: Exception) {
                // 네트워크 실패 시 닉네임 미표시
            }
        }
    }

    fun updateNickname(nickname: String) {
        val validationResult = NicknameValidator.validate(nickname)
        if (validationResult != NicknameValidationResult.Valid) {
            viewModelScope.launch {
                _nicknameToastEvent.emit("닉네임은 2~8자 한글/영문/숫자만 사용 가능합니다.")
            }
            return
        }

        viewModelScope.launch {
            _nicknameUpdateState.value = UiState.Loading

            accountRepository.updateNickname(nickname)
                .onSuccess {
                    _nickname.value = nickname
                    _nicknameUpdateState.value = UiState.Success(Unit)
                    _nicknameToastEvent.emit("닉네임이 변경되었습니다.")
                }
                .onFailure { e ->
                    _nicknameUpdateState.value = UiState.Failure(e.message ?: "닉네임 변경에 실패했습니다.")
                    _nicknameToastEvent.emit(e.message ?: "닉네임 변경에 실패했습니다.")
                }
        }
    }

    fun resetNicknameUpdateState() {
        _nicknameUpdateState.value = UiState.Idle
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = UiState.Loading

            accountRepository.deleteAccount()
                .onSuccess {
                    _deleteAccountState.value = UiState.Success(Unit)
                }
                .onFailure { e ->
                    _deleteAccountState.value =
                        UiState.Failure(e.message ?: "회원 탈퇴에 실패했습니다")
                }
        }
    }

}
