package com.egobook.app.store.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_item")
data class ShopItemEntity(
    @PrimaryKey val id: Int,
    val itemType: String,
    val price: Int,
    val thumbnailImageUrl: String,
    val equippedImageUrl: String
)
