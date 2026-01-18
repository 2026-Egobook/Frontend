package com.example.egobook.ui.home

import android.R.attr.level
import androidx.lifecycle.ViewModel
import com.example.egobook.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(UserState(Level(1), Ink(9999)))
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()
}