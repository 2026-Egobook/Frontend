package com.egobook.app.ui.shop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.user.Ink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    init {
        loadInk()
    }
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _ink = MutableStateFlow(Ink(0))
    val ink: StateFlow<Ink> = _ink.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()
    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems
    private val _items = MutableStateFlow<Map<ItemType, List<CustomItem>>>(mutableMapOf())
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
    fun loadItems(type: ItemType, forceRefresh: Boolean = false) {
        if (!forceRefresh && _items.value.containsKey(type)) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedList = mutableListOf<CustomItem>()
                storeRepository.loadStoreItems(type).collect { newItem ->
                    loadedList.add(newItem)
                }
                _items.update { currentMap ->
                    currentMap + (type to loadedList)
                }
            } catch (err: Exception) {
                Log.e("jang", "$err")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInk() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.load()
                _ink.update { user.ink }
            } catch (err: Exception) {
                Log.e("StoreViewModel", "잉크 로딩 실패", err)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadEquippedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _equippedItems.value = storeRepository.loadEquippedItems()
                Log.d("jang", "load: ${_equippedItems.value}")
            } catch (err: Exception) {
                Log.e("StoreViewModel", "장착 아이템 로딩 실패", err)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun equipItem(item: CustomItem) {
        _equippedItems.update { currentList ->
            currentList.filter { it.type != item.type } + item
        }

        if (item.itemStatus != ItemStatus.PURCHASABLE) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val result = storeRepository.permanentEquipItem(item)
                    if (result.isSuccess) {
                        Log.d("StoreViewModel", "서버 착용 성공: ${item.id}")
                        loadEquippedItems()
                    } else {
                        _toastEvent.emit("아이템(${item.id}) 착용에 실패했습니다.")
                    }
                } catch (e: Exception) {
                    Log.e("StoreViewModel", "서버 통신 에러", e)
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            Log.d("StoreViewModel", "미구매 아이템 - 프리뷰 모드")
        }
    }

    fun loadPurchaseItem(): CustomItem? {
        val purchasableItems = _equippedItems.value.filter { it.itemStatus == ItemStatus.PURCHASABLE }
        if (purchasableItems.isEmpty()){
            viewModelScope.launch {
                _toastEvent.emit("구매할 수 없는 상품입니다")
            }
            return null
        }
        return purchasableItems.first()
    }

    fun purchaseItem(item: CustomItem) {
        val purchasableItems = _equippedItems.value.filter { it.itemStatus == ItemStatus.PURCHASABLE }
        if (purchasableItems.isEmpty()) { return }
        val item = purchasableItems.first()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val purchaseState = storeRepository.purchaseItems(item)
                if (purchaseState.isSuccess) {
                    loadItems(item.type, true)
                    loadInk()
                    val purchasedItem = item.copy(itemStatus = ItemStatus.PURCHASED)
                    equipItem(purchasedItem)
                    _toastEvent.emit("구매가 완료되었어요")
                } else {
                    _toastEvent.emit("잉크가 부족해요")
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "구매 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun resetEquipItems() {
        loadEquippedItems()
    }
}
