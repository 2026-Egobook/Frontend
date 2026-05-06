package com.egobook.app.store.data.network.dto

data class ShopItemDto(
    val itemId: Int,
    val itemCategory: String?,
    val shopImageUrl: String,
    val myImageUrl: String,
    val price: Int,
    val isPurchased: Boolean,
    val isEquipped: Boolean
)
