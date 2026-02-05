package com.egobook.app.ui.shop

data class CustomItem(
    val id: String,
    val type: ItemType,
    val price: Price,
    val itemStatus: ItemStatus,
    val image: ItemImage? = null
)

sealed class ItemImage {
    data class Url(val path: String): ItemImage()
}