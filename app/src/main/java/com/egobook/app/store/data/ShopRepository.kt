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
import com.egobook.app.domain.model.square.letter.LetterPaperItem
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private fun String.toItemType(): ItemType = when (this) {
    "BACK" -> ItemType.BACK
    "SKIN" -> ItemType.SKIN
    "DECOR_ONE" -> ItemType.DECO_1
    "DECOR_TWO" -> ItemType.DECO_2
    "BACKGROUND" -> ItemType.BACKGROUND
    "LETTER_PAPER" -> ItemType.LETTER_PAPER
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
        if (!isEquipped && item.type in REQUIRED_ITEM_TYPES) {
            // 등껍질/고북은 비워둘 수 없으므로 해제 대신 기본 아이템으로 되돌린다.
            val defaultItem = findDefaultItem(item.type)
            if (defaultItem == null || defaultItem.id == item.id) return loadEquippedItems()
            return equipItemPermanently(defaultItem, true)
        }
        val result = remoteShopDataSource.permanentEquipItem(item, isEquipped)
        check(result.isSuccess) { "Failed to update equipped item" }
        remoteShopDataSource.confirmProfile()
        return loadEquippedItems()
    }

    suspend fun loadEquippedItems(): List<CustomItem> {
        val equippedItems = remoteShopDataSource.loadEquippedItems()
        // 이미 등껍질/고북 장착 정보가 비어있는 계정은 기본 아이템으로 보여준다.
        val defaultItems = REQUIRED_ITEM_TYPES
            .filter { type -> equippedItems.none { it.type == type } }
            .mapNotNull { findDefaultItem(it) }
        return equippedItems + defaultItems
    }

    private suspend fun findDefaultItem(type: ItemType): CustomItem? {
        val items = localShopDataSource.itemStream.first()
            .map { it.toDomain() }
            .filter { it.type == type }
            .ifEmpty { remoteShopDataSource.loadItems(type).map { it.toEntity().toDomain() } }
        return items.find { it.isDefaultImage() } ?: items.find { it.price.value == 0 }
    }

    private fun CustomItem.isDefaultImage(): Boolean =
        (outfitImage as? ItemImage.Url)?.path?.contains("Default") == true

    suspend fun loadLetterPaperItems(): List<LetterPaperItem> {
        return remoteShopDataSource.loadItems(ItemType.LETTER_PAPER).map { dto ->
            LetterPaperItem.fromDto(
                id = dto.itemId,
                price = dto.price,
                isPurchased = dto.isPurchased,
                shopImageUrl = dto.shopImageUrl,
                myImageUrl = dto.myImageUrl
            )
        }
    }

    suspend fun purchaseLetterPaperItem(item: LetterPaperItem) {
        val customItem = CustomItem(
            id = item.id.toString(),
            type = ItemType.LETTER_PAPER,
            price = Price(item.price),
            itemStatus = ItemStatus.PURCHASABLE
        )
        remoteShopDataSource.purchaseItems(customItem)
    }

    companion object {
        // 항상 무언가 장착되어 있어야 하는 카테고리
        private val REQUIRED_ITEM_TYPES = listOf(ItemType.BACK, ItemType.SKIN)
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
            image = ItemImage.Url(imageUrl),
            outfitImage = ItemImage.Url(imageUrl)
        )
    }
}
