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
import kotlinx.coroutines.CancellationException
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
    var latestConfirmedProfile: ProfileImageDto? = null
        private set

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

    suspend fun permanentEquipItem(item: CustomItem, isEquipped: Boolean): EquipState {
        try {
            apiService.equipItemPermanently(
                PermanentEquipRequest(itemId = item.id.toInt(), isEquipped = isEquipped)
            )
            return EquipState(isSuccess = true)
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            return EquipState(isSuccess = false)
        }
    }

    suspend fun confirmProfile() {
        val response = apiService.confirmProfile()
        val data = response.get("data")?.takeUnless { it.isJsonNull }?.asJsonObject ?: response
        check(data.has("turtleImageUrl") && data.has("backgroundImageUrl")) { "프로필 이미지 응답을 확인하지 못했습니다." }
        latestConfirmedProfile = com.google.gson.Gson().fromJson(data, ProfileImageDto::class.java)
    }
}

fun ItemType.ofName(): String =
    when (this) {
        ItemType.BACK -> "BACK"
        ItemType.SKIN -> "SKIN"
        ItemType.DECO_1 -> "DECOR_ONE"
        ItemType.DECO_2 -> "DECOR_TWO"
        ItemType.BACKGROUND -> "BACKGROUND"
        ItemType.LETTER_PAPER -> "LETTER_PAPER"
    }
