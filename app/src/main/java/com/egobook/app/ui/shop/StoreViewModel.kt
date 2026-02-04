package com.egobook.app.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {
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
}
