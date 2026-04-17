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
import com.egobook.app.store.ui.CustomItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userAdRepository: UserAdRepository,
    private val psychologyRepository: UserPsychologyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(User(id=-1, Level(1),Ink(0), nickname = ""))
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    private val _adState = MutableStateFlow(AdInfoDto(0, 0, false, 0, ""))
    val adState: StateFlow<AdInfoDto> = _adState.asStateFlow()

    private val _isLoadingAdInfo = MutableStateFlow(false)
    val isLoadingAdInfo: StateFlow<Boolean> = _isLoadingAdInfo.asStateFlow()

    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems

    private val _dailyPhycologyReadState = MutableStateFlow(false)
    val dailyPhycologyReadState = _dailyPhycologyReadState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch all initial data in parallel if possible, or sequentially
                val userJob = launch { fetchUserInternal() }
                // val equipJob = launch { fetchEquipItemsInternal() }
                val psychJob = launch { fetchDailyPhycologyReadStateInternal() }
                
                userJob.join()
                // equipJob.join()
                psychJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUserInternal() {
        try {
            val user = userRepository.load()
            _uiState.value = user
        } catch(error: Exception) {
            Log.e("HomeViewModel", "Failed to fetch user", error)
        }
    }

    /*
    private suspend fun fetchEquipItemsInternal() {
        try {
            _equippedItems.value = storeRepository.loadEquippedItems()
        } catch(error: Exception) {
            Log.e("HomeViewModel", "Failed to fetch equip items", error)
        }
    }
    */

    private suspend fun fetchDailyPhycologyReadStateInternal() {
        try {
            _dailyPhycologyReadState.value = psychologyRepository.isReadDailyPsychology()
        } catch(error: Exception) {
            Log.e("HomeViewModel", "Failed to fetch psychology state", error)
        }
    }

    fun fetchEquipItems() {
        viewModelScope.launch {
            // _isLoading.value = true
            // fetchEquipItemsInternal()
            // _isLoading.value = false
        }
    }

    fun fetchDailyPhycologyReadState() {
        viewModelScope.launch {
            _isLoading.value = true
            fetchDailyPhycologyReadStateInternal()
            _isLoading.value = false
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            _isLoading.value = true
            fetchUserInternal()
            _isLoading.value = false
        }
    }

    fun loadCurrentAdInfo() {
        viewModelScope.launch {
            _isLoadingAdInfo.value = true
            try {
                val adInfo = userAdRepository.loadAdInfo()
                _adState.value = adInfo
            } finally {
                _isLoadingAdInfo.value = false
            }
        }
    }

    fun watchAd() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val message = userAdRepository.watchAd()
                fetchUserInternal()
                Log.d("HomeViewModel", "Ad watched: $message")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
