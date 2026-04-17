package com.egobook.app.store.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.store.data.ShopRepository
import com.egobook.app.store.data.local.LocalShopDataSource
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.network.RemoteShopDataSource
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
    localShopDataSource: LocalShopDataSource,
    remoteShopDataSource: RemoteShopDataSource,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val shopRepository = ShopRepository(localShopDataSource, remoteShopDataSource)

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

    init {
        loadInk()
    }

    suspend fun initialize() {
        _isLoading.value = true
        try {
            shopRepository.initialize()
        } finally {
            _isLoading.value = false
        }
    }

    fun loadInk() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.load()
                _ink.update { user.ink }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Ink loading failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadEquippedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Assuming shopRepository has a way to get equipped items or they are streamed
                // For now, let's keep it consistent with the refactored architecture if possible
                // If ShopRepository doesn't have loadEquippedItems, we might need to add it or use remoteShopDataSource
                // remoteShopDataSource.loadEquippedItems()
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
                    shopRepository.equipItemPermanently(item)
                    Log.d("StoreViewModel", "서버 착용 성공: ${item.id}")
                    // loadEquippedItems()
                } catch (e: Exception) {
                    Log.e("StoreViewModel", "서버 통신 에러", e)
                    _toastEvent.emit("아이템(${item.id}) 착용에 실패했습니다.")
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            Log.d("StoreViewModel", "미구매 아이템 - 프리뷰 모드")
        }
    }

    fun loadPurchaseItem(): CustomItem? {
        val purchasableItems =
            _equippedItems.value.filter { it.itemStatus == ItemStatus.PURCHASABLE }
        if (purchasableItems.isEmpty()) {
            viewModelScope.launch {
                _toastEvent.emit("구매할 수 없는 상품입니다")
            }
            return null
        }
        return purchasableItems.first()
    }

    fun purchaseItem(item: CustomItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shopRepository.purchaseItem(item)
                loadInk()
                val purchasedItem = item.copy(itemStatus = ItemStatus.PURCHASED)
                equipItem(purchasedItem)
                _toastEvent.emit("구매가 완료되었어요")
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Purchase failed", e)
                _toastEvent.emit("잉크가 부족하거나 구매에 실패했어요")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetEquipItems() {
        loadEquippedItems()
    }
}
