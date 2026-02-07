package com.egobook.app.ui.home.user
enum class TendencyType {
    EMPATHY, SELF_ESTEEM, DILIGENCE, POSITIVE_THINKING, EMOTION_REGULATION;
}

data class Tendency(
    val type: TendencyType,
    val level: Int,
    val experiencePoint: Int
)
