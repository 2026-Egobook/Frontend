package com.egobook.app.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel()  {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init{

    }



    sealed class LoginEvent {

    }

    data class LoginUiState (
        val isLoading: Boolean = false,
        val successToSignIn: Boolean = false,
        val successLogin: Boolean = false
    )

}