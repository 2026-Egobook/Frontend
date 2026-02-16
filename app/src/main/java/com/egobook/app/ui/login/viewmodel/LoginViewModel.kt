package com.egobook.app.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.domain.usecase.authusecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val userInfoStorage: UserInfoStorage,
) : ViewModel()  {
    private val _isFirstSignUp = MutableSharedFlow<Unit>()
    val isFirstSignUp = _isFirstSignUp.asSharedFlow()
    private val _isGuestSignUp = MutableSharedFlow<Unit>()
    val isGuestSignUp = _isGuestSignUp.asSharedFlow()

    private val _signUpError = MutableSharedFlow<String>()
    val signUpError = _signUpError.asSharedFlow()

    private val _alreadyRegistered = MutableSharedFlow<Unit>()
    val alreadyRegistered = _alreadyRegistered.asSharedFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _autoLoginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val autoLoginState = _autoLoginState.asStateFlow()


    //"로그인 화면" 에서의 이벤트만 처리하는 용도.
    fun onEvent(event: LoginEvent) {
        when (event) {
            //최초 구글 회원가입일 시엔 _isFirstSignUp을 emit하여 온보딩 화면으로 이동하도록 설계
            is LoginEvent.TrySignInByGoogle -> {
                viewModelScope.launch {
                    _loginState.value = LoginState.Loading
                    val result = authUseCases.googleSignUp(event.idToken)
                    result.fold(
                        onSuccess = {
                            _isFirstSignUp.emit(Unit)
                            _loginState.value = LoginState.Idle  // 로딩 해제
                        },
                        onFailure = { error ->
                            val errorMessage = error.message ?: "알 수 없는 오류"
                            // 이미 가입된 계정인 경우
                            if (errorMessage.contains("이미") || errorMessage.contains("already", ignoreCase = true)) {
                                _alreadyRegistered.emit(Unit)
                            } else {
                                _signUpError.emit(errorMessage)
                            }
                            _loginState.value = LoginState.Idle  // 로딩 해제
                        }
                    )
                }
            }
            //구글 로그인 시도 -> 토큰 재발급 & 로그인 타입 저장
            is LoginEvent.TryLoginByGoogle -> {
                viewModelScope.launch {
                    _loginState.value = LoginState.Loading

                    val result = authUseCases.googleLogin(event.idToken)

                    result.fold(
                        onSuccess = {
                            _loginState.value = LoginState.Success

                        },
                        onFailure = { error ->
                            _loginState.value =
                                LoginState.Error(error.message ?: "알 수 없는 오류")
                        }
                    )
                }
            }

            // 게스트 로그인: 로그인 타입을 확인하여 최초/재로그인 분기
            is LoginEvent.TryGuestLogin -> {
                viewModelScope.launch{
                    _loginState.value = LoginState.Loading
                    
                    // 저장된 로그인 타입 확인
                    val loginType = userInfoStorage.getLoginType().first()
                    
                    val result = if (loginType == UserInfoStorage.LoginType.GUEST) {
                        // 재로그인: 토큰 재발급
                        authUseCases.guestReLogin()
                    } else {
                        // 최초 로그인: 신규 게스트 계정 생성
                        authUseCases.guestLogin()
                    }
                    result.fold(
                        onSuccess = {
                            if (loginType == UserInfoStorage.LoginType.GUEST) {
                                _loginState.value = LoginState.Success  // 재로그인 성공 -> 메인으로
                                Timber.d("재로그인 성공, 토큰 재발급")
                            } else {
                                _isGuestSignUp.emit(Unit)  // 최초 로그인 -> 온보딩으로
                                _loginState.value = LoginState.Idle
                                Timber.d("회원가입 성공, 토큰 발급")
                            }
                        },
                        onFailure = { error ->
                            _signUpError.emit(error.message ?: "알 수 없는 오류")
                            _loginState.value = LoginState.Idle
                        }
                    )
                }
            }
        }
    }

    sealed class LoginEvent {
        data class TrySignInByGoogle(val idToken: String) : LoginEvent() //회원가입
        data class TryLoginByGoogle(val idToken: String) : LoginEvent() //구글 로그인
        object TryGuestLogin : LoginEvent() //게스트 로그인 (최초/재로그인 자동 판단)
    }


    // 기존 계정이 있는 상태에서의 로그인 가정
    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data object Success : LoginState() //성공시 메인 화면으로
        data class Error(val message: String) : LoginState()
    }

}