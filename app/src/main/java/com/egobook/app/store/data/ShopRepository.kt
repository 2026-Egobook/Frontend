package com.egobook.app.store.data

import com.egobook.app.store.data.local.LocalShopDataSource
import com.egobook.app.store.data.local.entities.ShopItemEntity
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.data.network.RemoteShopDataSource
import com.egobook.app.store.data.network.dto.ShopItemDto
import com.egobook.app.store.data.network.ofName
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.ui.ItemImage
import javax.inject.Inject
import javax.inject.Singleton

private fun String.toItemType(): ItemType = when (this) {
    "BACK" -> ItemType.BACK
    "SKIN" -> ItemType.SKIN
    "DECOR_ONE" -> ItemType.DECO_1
    "DECOR_TWO" -> ItemType.DECO_2
    "BACKGROUND" -> ItemType.BACKGROUND
    else -> throw IllegalArgumentException("${this}은 알 수 없는 아이템 타입 이름입니다")
}

fun ShopItemDto.toEntity(): ShopItemEntity =
    ShopItemEntity(
        id = itemId,
        itemType = itemCategory ?: ItemType.BACKGROUND.ofName(),
        price = price,
        thumbnailImageUrl = shopImageUrl,
        equippedImageUrl = myImageUrl,
        isPurchased = isPurchased
    )


@Singleton
class ShopRepository @Inject constructor(
    private val localShopDataSource: LocalShopDataSource,
    private val remoteShopDataSource: RemoteShopDataSource
) {
    val itemStream = localShopDataSource.itemStream

    suspend fun initialize() {
        val itemEntities = ItemType.entries.flatMap { itemType ->
            remoteShopDataSource.loadItems(itemType).map {
                it.toEntity()
            }
        }

        localShopDataSource.initializeItems(itemEntities)
    }

    suspend fun purchaseItem(item: CustomItem) {
        remoteShopDataSource.purchaseItems(item)
        equipItemPermanently(item, true)
    }

    suspend fun equipItemPermanently(item: CustomItem, isEquipped: Boolean): List<CustomItem> {
        remoteShopDataSource.permanentEquipItem(item, isEquipped)
        return remoteShopDataSource.loadEquippedItems()
    }

    suspend fun loadEquippedItems(): List<CustomItem> {
        return remoteShopDataSource.loadEquippedItems()
    }
}

data class BaseResponse<T>(
    val code: String,
    val message: String,
    val status: Int,
    val data: T
)

data class PurchaseRequest(
    val itemId: Int
)

data class PermanentEquipRequest(
    val itemId: Int,
    val isEquipped: Boolean
)

data class EquipState(
    val isSuccess: Boolean
)

data class EquippedItemDto(
    val itemId: Int,
    val itemCategory: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean,
    val isEquipped: Boolean
) {
    fun toDomain(): CustomItem {
        val itemType = itemCategory.toItemType()
        return CustomItem(
            id = itemId.toString(),
            type = itemType,
            price = Price(price),
            itemStatus = if (isPurchased) ItemStatus.PURCHASED else ItemStatus.PURCHASABLE,
            image = null,
            outfitImage = ItemImage.Url(imageUrl)
        )
    }
}
