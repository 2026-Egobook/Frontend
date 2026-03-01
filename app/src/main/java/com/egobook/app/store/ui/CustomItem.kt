package com.egobook.app.store.ui

import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price

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
