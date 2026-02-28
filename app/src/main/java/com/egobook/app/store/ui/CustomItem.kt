package com.egobook.app.store.ui

import com.egobook.app.store.data.ItemStatus
import com.egobook.app.store.data.ItemType
import com.egobook.app.store.data.Price

data class CustomItem(
    val id: String,
    val type: ItemType,
    val price: Price,
    val itemStatus: ItemStatus,
    val image: ItemImage? = null,
    val outfitImage: ItemImage? = null
)

sealed class ItemImage {
    data class Url(val path: String): ItemImage()
}
