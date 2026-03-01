package com.egobook.app.store.data.network.dto

data class ShopItemGroupDto(
    val content: List<ShopItemDto>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)
