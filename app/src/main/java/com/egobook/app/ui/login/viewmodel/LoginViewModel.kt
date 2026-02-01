package com.egobook.app.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.domain.usecase.authusecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val userInfoStorage: UserInfoStorage
) : ViewModel()  {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _autoLoginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val autoLoginState = _autoLoginState.asStateFlow()


    //"로그인 화면" 에서의 이벤트만 처리하는 용도.
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.TryLoginByGoogle -> {
                TODO()
            }
            is LoginEvent.TryGuestLogin -> {
                TODO()
            }
        }
    }

    fun onAutoEvent(event: AutoEvent) {
        when (event) {
            is AutoEvent.TryAutoLoginByGoogle -> {
                viewModelScope.launch {
                    _autoLoginState.value = LoginState.Loading
                    val result = authUseCases.googleAutoLogin()
                    result.fold(
                        onSuccess = { _autoLoginState.value = LoginState.Success },
                        onFailure = { error -> _autoLoginState.value = LoginState.Error(error.message ?: "알 수 없는 오류") }
                    )
                }
            }
            is AutoEvent.TryAutoLoginByGuest -> {
                TODO()
            }
        }
    }

    sealed class LoginEvent {
        object TryLoginByGoogle : LoginEvent()
        object TryGuestLogin : LoginEvent()
    }

    //자동 로그인 이벤트 정의
    sealed class AutoEvent {
        object TryAutoLoginByGoogle : AutoEvent()
        object TryAutoLoginByGuest : AutoEvent()
    }

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

}