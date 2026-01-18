package com.example.egobook.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(UserState(Level(1), Ink(9999)))
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()
}