package com.egobook.app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.repository.AdInfoDto
import com.egobook.app.ui.home.repository.UserAdRepository
import com.egobook.app.ui.home.repository.UserPsychologyRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.user.Ink
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.shop.CustomItem
import com.egobook.app.ui.shop.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val userAdRepository: UserAdRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(User(id=-1, Level(1), Ink(0)))
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    private val _adState = MutableStateFlow(AdInfoDto(0, 0, false, 0, ""))
    val adState: StateFlow<AdInfoDto> = _adState.asStateFlow()

    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems
    private val psychologyRepository: UserPsychologyRepository
) : ViewModel() {

    init {
        fetchUser()
        fetchEquipItems()
        fetchDailyPhycologyReadState()
    }
    private val _uiState = MutableStateFlow(User(Level(1), Ink(0)))
    val uiState: StateFlow<User> = _uiState.asStateFlow()
    private val _dailyPhycologyReadState = MutableStateFlow(false)
    val dailyPhycologyReadState = _dailyPhycologyReadState.asStateFlow()
    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems

    fun fetchEquipItems() {
        viewModelScope.launch {
            _equippedItems.value = storeRepository.loadEquippedItems()
        }
    }

    fun fetchDailyPhycologyReadState() {
        viewModelScope.launch {
            _dailyPhycologyReadState.value = psychologyRepository.isReadDailyPsychology()
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

    fun loadCurrentAdInfo() {
        viewModelScope.launch {
            val adInfo = userAdRepository.loadAdInfo()
            _adState.value = adInfo
        }
    }

    fun watchAd() {
        viewModelScope.launch {
            val message = userAdRepository.watchAd()
            fetchUser()
            Log.d("HomeViewModel", "Ad watched: $message")
        }
    }
}
