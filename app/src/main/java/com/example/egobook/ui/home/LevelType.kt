package com.example.egobook.ui.home

enum class LevelType(val range: IntRange) {
    ONE(0..99),
    TWO(100..299),
    THREE(300..499),
    FOUR(500..699),
    FIVE(700..999),
    SIX(1000..1299),
    SEVEN(1300..1499),
    EIGHT(1500..1500);

    companion object {
        fun of(number: Int): LevelType = checkNotNull(entries.find { number in it.range }) {
            "잘못된 레벨 값입니다."
        }
    }
}