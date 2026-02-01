package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.AbusiveContentAnalysis
import com.google.gson.annotations.SerializedName

data class DetectAbusiveContentResponse(
    @SerializedName("text")
    val text: String,
    @SerializedName("percentage")
    val percentage: Double,
    @SerializedName("is_harmful")
    val isHarmful: Boolean,
    @SerializedName("label")
    val label: String,
    @SerializedName("bad_words")
    val badWords: List<String>
)

fun DetectAbusiveContentResponse.toDomain(): AbusiveContentAnalysis = AbusiveContentAnalysis(
    text = text,
    riskScore = percentage,
    isHarmful = isHarmful,
    label = label,
    detectedBadWords = badWords
)