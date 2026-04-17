package com.egobook.app.store.ui

import android.os.Parcelable
import com.egobook.app.store.data.model.ItemType
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ItemTab(val type: ItemType, val position: Int, val text: String) : Parcelable {
    BACK(ItemType.BACK, 0, "등껍질"),
    SKIN(ItemType.SKIN, 1, "고북"),
    DECO_1(ItemType.DECO_1, 2, "데코 1"),
    DECO_2(ItemType.DECO_2, 3, "데코 2"),
    BACKGROUND(ItemType.BACKGROUND, 4, "배경");

    companion object {
        const val BUNDLE_KEY = "itemTab"
        fun of(position: Int): ItemTab {
            return checkNotNull(entries.find { it.position == position }) {
                "올바르지 못한 포지션 값입니다."
            }
        }
    }
}
