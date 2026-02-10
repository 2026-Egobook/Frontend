package com.egobook.app.ui.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomItemState(
    val isSelected: Boolean,
    val item: CustomItem
)
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {
    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems
    private val _items = MutableStateFlow<Map<ItemType, List<CustomItem>>>(mutableMapOf())
    val items: StateFlow<Map<ItemType, List<CustomItem>>> = _items.asStateFlow()

    val itemStates: StateFlow<Map<ItemType, List<CustomItemState>>> =
        combine(_items, _equippedItems) { itemsMap, equippedList ->

            val equippedIds = equippedList.map { it.id }.toSet()

            itemsMap.mapValues { (_, items) ->
                items.map { item ->
                    CustomItemState(
                        item = item,
                        isSelected = equippedIds.contains(item.id)
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
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
            Log.d("jang", "load: ${_equippedItems.value}")
        }

    }

    fun equipItem(item: CustomItem) {
        _equippedItems.update { currentList ->
            currentList.filter { it.type != item.type } + item
        }
    }

    fun resetEquipItems() {
        _equippedItems.value = emptyList()
    }
}
