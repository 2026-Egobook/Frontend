package com.egobook.app.ui.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {
    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems
    private val _items = MutableStateFlow<Map<ItemType, List<CustomItem>>>(mutableMapOf())
    val items: StateFlow<Map<ItemType, List<CustomItem>>> = _items.asStateFlow()
    fun loadItems(type: ItemType) {
        if (_items.value.containsKey(type)) return
        viewModelScope.launch {
            storeRepository.loadStoreItems(type)
                .collect { newItem ->
                    val loadedList = mutableListOf<CustomItem>()
                    storeRepository.loadStoreItems(type).collect { newItem ->
                        loadedList.add(newItem)
                    }

                    _items.update { currentMap ->
                        currentMap + (type to loadedList)
                    }
                }
        }
    }

    fun loadEquippedItems() {
        viewModelScope.launch {
            _equippedItems.value = storeRepository.loadEquippedItems()
        }
    }

    fun equipItem(item: CustomItem) {
        _equippedItems.value = _equippedItems.value.filter { it.type != item.type } + item
    }

    fun resetEquipItems() {
        _equippedItems.value = emptyList()
    }
}
