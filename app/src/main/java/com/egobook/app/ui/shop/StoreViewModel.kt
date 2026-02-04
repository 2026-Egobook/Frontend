package com.egobook.app.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreViewModel(val storeRepository: StoreRepository): ViewModel() {
    private val _items = MutableStateFlow<List<CustomItem>>(emptyList())
    val items: StateFlow<List<CustomItem>> = _items.asStateFlow()
    fun loadItems(type: ItemType) {
        viewModelScope.launch {
            _items.value = emptyList()
            storeRepository.loadStoreItems(type)
                .collect { newItem ->
                    _items.value += newItem
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return StoreViewModel(NetworkStoreRepository()) as T
            }
        }
    }
}
