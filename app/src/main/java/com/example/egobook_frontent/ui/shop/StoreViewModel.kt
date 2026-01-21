package com.example.egobook_frontent.ui.shop

import androidx.lifecycle.ViewModel

class StoreViewModel: ViewModel() {
    val temporalData = listOf(
        CustomItem("1", ItemType.BACK, Price(100), ItemStatus.PURCHASABLE),
        CustomItem("2", ItemType.BACK, Price(50), ItemStatus.PURCHASED),
        CustomItem("3", ItemType.BACK, Price(40), ItemStatus.PURCHASABLE),
        CustomItem("4", ItemType.BACK, Price(150), ItemStatus.SUBSCRIBE_ONLY),
        CustomItem("5", ItemType.BACK, Price(30), ItemStatus.PURCHASABLE),
        CustomItem("6", ItemType.BACK, Price(10), ItemStatus.PURCHASED),
        CustomItem("7", ItemType.BACK, Price(190), ItemStatus.SUBSCRIBE_ONLY),
        CustomItem("8", ItemType.BACK, Price(200), ItemStatus.PURCHASED),
        CustomItem("9", ItemType.BACK, Price(300), ItemStatus.SUBSCRIBE_ONLY),
        CustomItem("10", ItemType.DECO_1, Price(590), ItemStatus.SUBSCRIBE_ONLY),
        CustomItem("11", ItemType.DECO_2, Price(600), ItemStatus.PURCHASED),
        CustomItem("12", ItemType.SKIN, Price(700), ItemStatus.SUBSCRIBE_ONLY),
        CustomItem("13", ItemType.BACKGROUND, Price(222), ItemStatus.PURCHASABLE),
        CustomItem("14", ItemType.BACKGROUND, Price(444), ItemStatus.PURCHASABLE),
    )
    fun loadItems(type: ItemType): List<CustomItem> {
        return temporalData.filter { customItem ->
            customItem.type == type
        }
    }
}
