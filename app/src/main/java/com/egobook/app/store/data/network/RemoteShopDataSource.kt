package com.egobook.app.store.data.network

import com.egobook.app.di.qualifier.BackendApi
import com.egobook.app.store.data.BaseResponse
import com.egobook.app.store.data.EquipState
import com.egobook.app.store.data.EquippedItemDto
import com.egobook.app.store.data.PermanentEquipRequest
import com.egobook.app.store.data.PurchaseRequest
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.network.dto.ShopItemDto
import com.egobook.app.store.ui.CustomItem
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

data class ShopItemDtoSlice(
    val shopItemDtoGroup: List<ShopItemDto>,
    val hasNext: Boolean
)

@Singleton
class RemoteShopDataSource @Inject constructor(
    @BackendApi private val retrofit: Retrofit
) {
    private val apiService by lazy {
        retrofit.create(ShopApiService::class.java)
    }

    private suspend fun loadSpecificSliceItems(
        itemType: ItemType,
        slice: Int,
        size: Int = 6
    ): ShopItemDtoSlice {
        val response = apiService.loadAllItems(
            category = itemType.ofName(),
            slice = slice,
            size = size
        ).data
        return ShopItemDtoSlice(shopItemDtoGroup = response.content, hasNext = response.hasNext)
    }

    suspend fun loadItems(itemType: ItemType): List<ShopItemDto> {
        var currentPage = 1
        val totalShopItemDto = mutableListOf<ShopItemDto>()
        while (true) {
            val shopItemDtoSlice = loadSpecificSliceItems(itemType, slice = currentPage)
            shopItemDtoSlice.shopItemDtoGroup.forEach { shopItemDto ->
                totalShopItemDto.add(shopItemDto)
            }
            if (!shopItemDtoSlice.hasNext) {
                break
            }
            currentPage++
        }
        return totalShopItemDto
    }

    suspend fun purchaseItems(item: CustomItem) {
        apiService.purchaseItem(PurchaseRequest(item.id.toInt()))
    }

    suspend fun loadEquippedItems(): List<CustomItem> {
        val equippedItemsResponse: BaseResponse<List<EquippedItemDto>> =
            apiService.loadEquippedItems()
        return equippedItemsResponse.data.map { it.toDomain() }
    }

    suspend fun permanentEquipItem(item: CustomItem): EquipState {
        try {
            apiService.equipItemPermanently(
                PermanentEquipRequest(itemId = item.id.toInt(), isEquipped = true)
            )
            return EquipState(isSuccess = true)
        } catch (err: Exception) {
            return EquipState(isSuccess = false)
        }
    }
}

fun ItemType.ofName(): String =
    when (this) {
        ItemType.BACK -> "BACK"
        ItemType.SKIN -> "SKIN"
        ItemType.DECO_1 -> "DECOR_ONE"
        ItemType.DECO_2 -> "DECOR_TWO"
        ItemType.BACKGROUND -> "BACKGROUND"
    }
