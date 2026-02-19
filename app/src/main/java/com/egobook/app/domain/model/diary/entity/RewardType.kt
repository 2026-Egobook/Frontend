package com.egobook.app.domain.model.diary.entity

enum class RewardType(val value: String, val displayType: String) {
    INK("INK", "잉크"),
    EMOTION_REGULATION("EMOTION_REGULATION", "감정조절"),
    POSITIVE_THINKING("POSITIVE_THINKING", "긍정사고");

    companion object {
        /**
         * API value로 보상 타입 찾기
         */
        fun from(value: String): RewardType {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown reward type: $value")
        }
    }
}
