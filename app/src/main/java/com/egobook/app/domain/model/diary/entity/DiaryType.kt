package com.egobook.app.domain.model.diary.entity

enum class DiaryType(val value: String, val displayType: String) {
    EMOTION("EMOTION", "감정"),
    CONCERN("CONCERN", "고민"),
    PRAISE("PRAISE", "칭찬"),
    GRATITUDE("GRATITUDE", "감사");
    companion object {
        /**
         * API value로 일기 타입 찾기 (예: "EMOTION", "CONCERN")
         */
        fun from(value: String): DiaryType {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown diary type: $value")
        }

        /**
         * 한글 displayType으로 DiaryType 찾기 (예: "감정", "고민")
         */
        fun fromDisplayType(displayType: String): DiaryType {
            return entries.find { it.displayType == displayType }
                ?: throw IllegalArgumentException("Unknown display type: $displayType")
        }

    }
}