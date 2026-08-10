package com.egobook.app.ui.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.domain.model.account.NicknameValidationResult
import com.egobook.app.domain.model.account.NicknameValidator
import com.egobook.app.domain.model.account.WithdrawReasonType
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.egobook.app.domain.model.auth.AuthError

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
    private val analyticsLogger: AnalyticsLogger
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

    private val _selectedWithdrawReason = MutableStateFlow<WithdrawReasonType?>(null)
    val selectedWithdrawReason = _selectedWithdrawReason.asStateFlow()

    private val _withdrawReasonText = MutableStateFlow("")
    val withdrawReasonText = _withdrawReasonText.asStateFlow()

    private val _withdrawToastEvent = MutableSharedFlow<String>(replay = 0)
    val withdrawToastEvent = _withdrawToastEvent.asSharedFlow()

    /**
     * 사유를 골랐고, 기타인 경우 상세 사유까지 입력했는지
     *
     * 구독자가 없는 동안에도 [deleteAccount]가 최신 값을 읽어야 하므로 Eagerly로 공유한다.
     */
    val isWithdrawReasonValid = combine(
        _selectedWithdrawReason,
        _withdrawReasonText
    ) { reason, text ->
        isReasonValid(reason, text)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private fun isReasonValid(reason: WithdrawReasonType?, text: String): Boolean =
        when (reason) {
            null -> false
            WithdrawReasonType.OTHER -> text.isNotBlank()
            else -> true
        }

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
            analyticsLogger.logEvent(AnalyticsEvent.ACCOUNT_LINK_START)

            accountRepository.linkToGoogle(idToken)
                .onSuccess {
                    _linkState.value = UiState.Success(Unit)
                    analyticsLogger.logEvent(AnalyticsEvent.ACCOUNT_LINK_COMPLETE)
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
                    analyticsLogger.logEvent(AnalyticsEvent.NICKNAME_CHANGE)
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

    fun selectWithdrawReason(reason: WithdrawReasonType) {
        _selectedWithdrawReason.value = reason
        // 기타 외의 사유로 바꾸면 입력해 둔 상세 사유는 버린다
        if (reason != WithdrawReasonType.OTHER) {
            _withdrawReasonText.value = ""
        }
    }

    fun updateWithdrawReasonText(text: String) {
        _withdrawReasonText.value = text.take(WithdrawReasonType.MAX_TEXT_LENGTH)
    }

    fun resetWithdrawReason() {
        _selectedWithdrawReason.value = null
        _withdrawReasonText.value = ""
        _deleteAccountState.value = UiState.Idle
    }

    /**
     * 선택한 사유를 저장한 뒤 회원 탈퇴를 진행한다.
     *
     * 탈퇴 API를 먼저 호출하면 사유가 저장되지 않으므로 순서를 바꾸면 안 된다.
     * 사유 저장 실패는 탈퇴 자체를 막지 않는다.
     */
    fun deleteAccount() {
        val reason = _selectedWithdrawReason.value
        if (reason == null || !isReasonValid(reason, _withdrawReasonText.value)) {
            viewModelScope.launch {
                _withdrawToastEvent.emit(
                    if (reason == WithdrawReasonType.OTHER) "탈퇴 사유를 작성해 주세요"
                    else "탈퇴 이유를 선택해 주세요"
                )
            }
            return
        }

        viewModelScope.launch {
            _deleteAccountState.value = UiState.Loading
            analyticsLogger.logEvent(AnalyticsEvent.ACCOUNT_WITHDRAW_REQUEST)

            accountRepository.submitWithdrawReason(reason, _withdrawReasonText.value)
                .onFailure { e ->
                    Timber.w(e, "탈퇴 사유 저장 실패, 탈퇴는 계속 진행합니다")
                }

            accountRepository.deleteAccount()
                .onSuccess {
                    _deleteAccountState.value = UiState.Success(Unit)
                    analyticsLogger.logEvent(
                        AnalyticsEvent.ACCOUNT_WITHDRAW_CONFIRM,
                        mapOf(ANALYTICS_PARAM_REASON_TYPE to reason.value)
                    )
                }
                .onFailure { e ->
                    val message = e.message ?: "회원 탈퇴에 실패했습니다"
                    _deleteAccountState.value = UiState.Failure(message)
                    _withdrawToastEvent.emit(message)
                }
        }
    }

    companion object {
        private const val ANALYTICS_PARAM_REASON_TYPE = "reason_type"
    }

}
