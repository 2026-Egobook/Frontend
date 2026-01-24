package com.egobook.app.domain.model


import androidx.annotation.StringRes
import com.egobook.app.R

//더미데이터

enum class DiaryType(@StringRes val displayName: Int) {
    Emotion(R.string.emotion),
    Worry(R.string.worry),
    Praise(R.string.praise),
    Thanks(R.string.thanks)
}
data class Diary(
    val content: String,
    val types: List<DiaryType>?,
    val time: String
)
