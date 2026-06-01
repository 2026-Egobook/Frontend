package com.egobook.app.domain.model.square.letter

data class LetterPaperItem(
    val id: Int,
    val price: Int,
    val isPurchased: Boolean,
    val color: LetterBackgroundColor
) {
    companion object {
        fun fromDto(id: Int, price: Int, isPurchased: Boolean, imageUrl: String): LetterPaperItem {
            val color = when {
                imageUrl.contains("Pink", ignoreCase = true) -> LetterBackgroundColor.PINK
                imageUrl.contains("Green", ignoreCase = true) -> LetterBackgroundColor.GREEN
                imageUrl.contains("Blue", ignoreCase = true) -> LetterBackgroundColor.BLUE
                imageUrl.contains("Purple", ignoreCase = true) -> LetterBackgroundColor.PURPLE
                else -> throw IllegalArgumentException("알 수 없는 편지지 색상 URL: $imageUrl")
            }
            return LetterPaperItem(id = id, price = price, isPurchased = isPurchased, color = color)
        }
    }
}
