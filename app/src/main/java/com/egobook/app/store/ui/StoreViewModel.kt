package com.egobook.app.store.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
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
import timber.log.Timber

data class CustomItemState(
    val isSelected: Boolean,
    val item: CustomItem
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    fun logShopOpen() {
        analyticsLogger.logEvent(AnalyticsEvent.SHOP_OPEN)
    }

    fun logShopExit() {
        analyticsLogger.logEvent(AnalyticsEvent.SHOP_EXIT)
    }

    private var loadingCount = 0
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun showLoading() {
        loadingCount++
        _isLoading.value = true
    }

    private fun hideLoading() {
        loadingCount--
        if (loadingCount <= 0) {
            loadingCount = 0
            _isLoading.value = false
        }
    }

    private val _ink = MutableStateFlow(Ink(0))
    val ink: StateFlow<Ink> = _ink.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // 서버에 실제로 장착된 아이템들
    private val _permanentItems = MutableStateFlow<List<CustomItem>>(emptyList())
    // 화면에 보여줄 아이템들 (미리보기 포함)
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
    }

    private fun observeItems() {
        viewModelScope.launch {
            shopRepository.itemStream.collect { entities ->
                val domainItems = entities.map { it.toDomain() }
                val groupedItems = domainItems.groupBy { it.type }
                _items.update { groupedItems }
            }
        }
    }

    suspend fun initialize() {
        showLoading()
        try {
            loadInkInternal()
            observeItems()
            shopRepository.initialize()
            val permanent = shopRepository.loadEquippedItems()
            _permanentItems.value = permanent
            _equippedItems.value = permanent
        } catch (e: Exception) {
            Timber.e(e, "Store initialize failed")
            _toastEvent.emit("상점 정보를 불러오지 못했습니다.")
        } finally {
            hideLoading()
        }
    }

    private suspend fun loadInkInternal() {
        try {
            val user = userRepository.load()
            _ink.update { user.ink }
        } catch (e: Exception) {
            Timber.e(e, "Ink loading failed")
        }
    }

    fun loadInk() {
        viewModelScope.launch {
            showLoading()
            loadInkInternal()
            hideLoading()
        }
    }

    fun loadEquippedItems() {
        viewModelScope.launch {
            showLoading()
            try {
                val permanent = shopRepository.loadEquippedItems()
                _permanentItems.value = permanent
                _equippedItems.value = permanent
                Timber.d("load: ${permanent}")
            } catch (e: Exception) {
                Timber.e(e, "Load equipped items failed")
            } finally {
                hideLoading()
            }
        }
    }

    fun equipItem(item: CustomItem) {
        if (item.itemStatus == ItemStatus.PURCHASABLE) {
            // 미구매 아이템: 프리뷰 모드. 같은 타입의 기존 아이템만 제거하고 현재 아이템 추가
            _equippedItems.update { currentList ->
                currentList.filter { it.type != item.type } + item
            }
            Timber.d("미구매 아이템 - 프리뷰 모드")
        } else {
            // 구매한 아이템: 서버에 장착/해제 요청 (토글)
            viewModelScope.launch {
                showLoading()
                try {
                    val isAlreadyEquipped = _permanentItems.value.any { it.id == item.id }
                    val updatedEquipped = shopRepository.equipItemPermanently(item, !isAlreadyEquipped)
                    _permanentItems.value = updatedEquipped
                    _equippedItems.value = updatedEquipped
                    analyticsLogger.logEvent(
                        AnalyticsEvent.ITEM_EQUIP,
                        mapOf(
                            AnalyticsParam.ITEM_ID to item.id,
                            AnalyticsParam.ITEM_TYPE to item.type.name
                        )
                    )
                    Timber.d("서버 착용 상태 변경 성공: ${item.id}, ${!isAlreadyEquipped}")
                } catch (e: Exception) {
                    Timber.e(e, "서버 통신 에러")
                    _toastEvent.emit("아이템(${item.id}) 상태 변경에 실패했습니다.")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    fun clearPreviewItems() {
        // 임시 착용 아이템을 벗고 서버에 저장된 원래 상태로 복구
        _equippedItems.value = _permanentItems.value
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
            showLoading()
            try {
                shopRepository.purchaseItem(item)
                shopRepository.initialize()
                loadInk()
                // 구매 성공 후 최신 장착 정보 다시 로드
                loadEquippedItems()
                analyticsLogger.logEvent(
                    AnalyticsEvent.ITEM_PURCHASE,
                    mapOf(
                        AnalyticsParam.ITEM_ID to item.id,
                        AnalyticsParam.ITEM_TYPE to item.type.name,
                        AnalyticsParam.PRICE to item.price.value
                    )
                )
                _toastEvent.emit("구매가 완료되었어요")
            } catch (e: Exception) {
                Timber.e(e, "Purchase failed")
                _toastEvent.emit("잉크가 부족하거나 구매에 실패했어요")
            } finally {
                hideLoading()
            }
        }
    }

    fun resetEquipItems() {
        loadEquippedItems()
    }
}
