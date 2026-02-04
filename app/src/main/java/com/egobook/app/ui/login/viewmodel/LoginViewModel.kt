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
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
) : ViewModel()  {
    private val _isFirstSignUp = MutableSharedFlow<Unit>()
    val isFirstSignUp = _isFirstSignUp.asSharedFlow()
    private val _isGuestSignUp = MutableSharedFlow<Unit>()
    val isGuestSignUp = _isGuestSignUp.asSharedFlow()

    private val _signUpError = MutableSharedFlow<String>()
    val signUpError = _signUpError.asSharedFlow()

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
                            _signUpError.emit(error.message ?: "알 수 없는 오류")
                            _loginState.value = LoginState.Idle  // 로딩 해제
                        }
                    )
                }
            }
            is LoginEvent.TryLoginByGoogle -> {
                viewModelScope.launch {
                    _loginState.value = LoginState.Loading
                    val result = authUseCases.googleLogin(event.idToken)
                    result.fold(
                        onSuccess = { _loginState.value = LoginState.Success },
                        onFailure = { error -> _loginState.value = LoginState.Error(error.message ?: "알 수 없는 오류") }
                    )
                }
            }
            //게스트 로그인(회원가입)일 시엔 _isGuestSignUp을 emit하여 온보딩 화면으로 이동하도록 설계
            is LoginEvent.TryGuestLogin -> {
                viewModelScope.launch{
                    _loginState.value = LoginState.Loading
                    val result = authUseCases.guestLogin()
                    result.fold(
                        onSuccess = {
                            _isGuestSignUp.emit(Unit)
                            _loginState.value = LoginState.Idle  // 로딩 해제
                        },
                        onFailure = { error ->
                            _signUpError.emit(error.message ?: "알 수 없는 오류")
                            _loginState.value = LoginState.Idle  // 로딩 해제
                        }
                    )
                }
            }
        }
    }

    sealed class LoginEvent {
        data class TrySignInByGoogle(val idToken: String) : LoginEvent() //회원가입
        data class TryLoginByGoogle(val idToken: String) : LoginEvent() //구글 로그인
        object TryGuestLogin : LoginEvent() //게스트 로그인
    }


    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

}