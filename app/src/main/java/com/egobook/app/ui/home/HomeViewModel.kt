package com.egobook.app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.user.Ink
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.shop.CustomItem
import com.egobook.app.ui.shop.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(User(id=-1, Level(1), Ink(0)))
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems

    init {
        fetchUser()
        fetchEquipItems()
    }

    fun fetchEquipItems() {
        viewModelScope.launch {
            _equippedItems.value = storeRepository.loadEquippedItems()
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val user = userRepository.load()
                _uiState.value = user
            } catch(error: Exception) {
                Log.e("HomeViewModel", "Failed to fetch user", error)
            }
        }
    }
}
