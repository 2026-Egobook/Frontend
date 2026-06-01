package com.egobook.app.store.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.ui.ItemImage

@Entity(tableName = "shop_item")
data class ShopItemEntity(
    @PrimaryKey val id: Int,
    val itemType: String,
    val price: Int,
    val thumbnailImageUrl: String,
    val equippedImageUrl: String,
    val isPurchased: Boolean
) {
    fun toDomain(): CustomItem {
        val type = when (itemType) {
            "BACK" -> ItemType.BACK
            "SKIN" -> ItemType.SKIN
            "DECOR_ONE" -> ItemType.DECO_1
            "DECOR_TWO" -> ItemType.DECO_2
            "BACKGROUND" -> ItemType.BACKGROUND
            "LETTER_PAPER" -> ItemType.LETTER_PAPER
            else -> ItemType.BACKGROUND
        }
        return CustomItem(
            id = id.toString(),
            type = type,
            price = Price(price),
            itemStatus = if (isPurchased) ItemStatus.PURCHASED else ItemStatus.PURCHASABLE,
            image = ItemImage.Url(thumbnailImageUrl),
            outfitImage = ItemImage.Url(equippedImageUrl)
        )
    }
}
