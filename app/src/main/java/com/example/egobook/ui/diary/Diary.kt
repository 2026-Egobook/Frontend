package com.example.egobook.ui.diary


import androidx.annotation.StringRes
import com.example.egobook.R

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
