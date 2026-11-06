package com.egobook.app.ui.diary.util

import androidx.annotation.DrawableRes
import com.egobook.app.R

/**
 * 감정 레벨(1~5)에 해당하는 감정 이미지 리소스를 반환한다.
 */
@DrawableRes
fun emotionImageRes(emotionLevel: Int): Int =
    when (emotionLevel) {
        1 -> R.drawable.img_emotion_very_sad
        2 -> R.drawable.img_emotion_sad
        3 -> R.drawable.img_emotion_neutral
        4 -> R.drawable.img_emotion_happy
        5 -> R.drawable.img_emotion_very_happy
        else -> R.drawable.img_emotion_neutral
    }
