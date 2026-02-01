package com.egobook.app.domain.model.square.letter

data class AbusiveContentAnalysis(
    val text: String,
    val riskScore: Double,
    val isHarmful: Boolean,
    val label: String,
    val detectedBadWords: List<String>
)
