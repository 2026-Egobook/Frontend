package com.egobook.app.domain.model.square.letter

data class LetterPaperItem(
    val id: Int,
    val price: Int,
    val isPurchased: Boolean,
    val color: LetterBackgroundColor,
    val imageUrl: String
) {
    companion object {
        fun fromDto(id: Int, price: Int, isPurchased: Boolean, shopImageUrl: String, myImageUrl: String = ""): LetterPaperItem {
            val color = when {
                shopImageUrl.contains("Pink", ignoreCase = true) -> LetterBackgroundColor.PINK
                shopImageUrl.contains("Green", ignoreCase = true) -> LetterBackgroundColor.GREEN
                shopImageUrl.contains("Blue", ignoreCase = true) -> LetterBackgroundColor.BLUE
                shopImageUrl.contains("Purple", ignoreCase = true) -> LetterBackgroundColor.PURPLE
                else -> throw IllegalArgumentException("알 수 없는 편지지 색상 URL: $shopImageUrl")
            }
            return LetterPaperItem(id = id, price = price, isPurchased = isPurchased, color = color, imageUrl = shopImageUrl.replace("letter/letter/", "letter/"))
        }
    }
}
