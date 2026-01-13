package com.example.egobook_frontent.ui.home

import com.example.egobook_frontent.R

enum class LevelType(val range: IntRange, val badgeUiId: Int) {
    ONE(0..99, R.drawable.level_type_1),
    TWO(100..299, R.drawable.level_type_2),
    THREE(300..499, R.drawable.level_type_3),
    FOUR(500..699, R.drawable.level_type_4),
    FIVE(700..999, R.drawable.level_type_5),
    SIX(1000..1299, R.drawable.level_type_6),
    SEVEN(1300..1499, R.drawable.level_type_7),
    EIGHT(1500..1500, R.drawable.level_type_8);

    companion object {
        fun of(number: Int): LevelType = checkNotNull(entries.find { number in it.range }) {
            "잘못된 레벨 값입니다."
        }
    }
}
