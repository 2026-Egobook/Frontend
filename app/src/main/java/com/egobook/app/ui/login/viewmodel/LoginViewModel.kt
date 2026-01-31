package com.egobook.app.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.domain.usecase.authusecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val userInfoStorage: UserInfoStorage
) : ViewModel()  {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState = _loginUiState.asStateFlow()

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

    //최초 회원가입 전용
    suspend fun signUpWithGoogleToken(idToken: String) {
        _loginUiState.value = LoginUiState.Loading
        try {
            // DateStore에 ID 토큰 저장
            userInfoStorage.saveIdToken(idToken)
            Log.d("GOOGLE_TOKEN", "idToken = $idToken")

            // API 요청
            val result = authUseCases.googleSignUp()
            result.fold(
                onSuccess = { _loginUiState.value = LoginUiState.Success },
                onFailure = { error ->
                    _loginUiState.value = LoginUiState.Error(error.message ?: "회원가입 실패")
                }
            )
        } catch (e: Exception) {
            _loginUiState.value = LoginUiState.Error(e.message ?: "알 수 없는 오류")
        }
    }




    sealed class LoginEvent {
        object TryLoginByGoogle : LoginEvent()
        object TryGuestLogin : LoginEvent()

    }

    sealed class LoginUiState {
        data object Idle : LoginUiState()
        data object Loading : LoginUiState()
        data object Success : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

}