package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis

data class AbusiveContentModel(
    val text: String,
    val riskScore: Double,
    val isHarmful: Boolean,
    val label: String,
    val detectedBadWords: List<String>
)

fun AbusiveContentAnalysis.toPresentation(): AbusiveContentModel = AbusiveContentModel(
    text = text,
    riskScore = riskScore,
    isHarmful = isHarmful,
    label = label,
    detectedBadWords = detectedBadWords
)
