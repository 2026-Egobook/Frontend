package com.egobook.app.ui.home.repository

import com.egobook.app.ui.home.user.Tendency
import com.egobook.app.ui.home.user.TendencyType

data class TendencyLevels(
    val empathy: TendencyLevelDto,
    val selfEsteem: TendencyLevelDto,
    val diligence: TendencyLevelDto,
    val positiveThinking: TendencyLevelDto,
    val emotionRegulation: TendencyLevelDto
) {
    fun toDomain(): List<Tendency> {
        return listOf(
            Tendency(TendencyType.EMPATHY, empathy.level, empathy.score),
            Tendency(TendencyType.SELF_ESTEEM, selfEsteem.level, selfEsteem.score),
            Tendency(TendencyType.DILIGENCE, diligence.level, diligence.score),
            Tendency(TendencyType.POSITIVE_THINKING, positiveThinking.level, positiveThinking.score),
            Tendency(TendencyType.EMOTION_REGULATION, emotionRegulation.level, emotionRegulation.score)
        )

    }
}

data class TendencyLevelDto(
    val level: Int,
    val score: Int,
    val color: String
)
