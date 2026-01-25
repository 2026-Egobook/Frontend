package com.egobook.app.ui.diary.mapper

import androidx.annotation.DrawableRes
import com.egobook.app.R
import com.egobook.app.domain.model.EmotionLevel

object ImageMapper {

    /**
     * [EmotionLevel]을 UI에 표시할 이미지 리소스 ID로 변환합니다.
     * 감정 일기가 아닌 경우 (emotionLevel이 null) 기본 이미지를 반환할 수 있도록 nullable을 받습니다.
     *
     * @param emotionLevel 변환할 감정 레벨 (nullable)
     * @return 해당하는 이미지의 Drawable 리소스 ID. 매칭되는 레벨이 없거나 null이면 기본 이미지를 반환합니다.
     */
    @DrawableRes
    fun toEmotionImage(emotionLevel: EmotionLevel?): Int? {
        return when (emotionLevel) {
            EmotionLevel.VERY_BAD -> R.drawable.img_emotion_very_sad
            EmotionLevel.BAD -> R.drawable.img_emotion_sad
            EmotionLevel.NORMAL -> R.drawable.img_emotion_neutral
            EmotionLevel.GOOD -> R.drawable.img_emotion_happy
            EmotionLevel.VERY_GOOD -> R.drawable.img_emotion_very_happy
            null -> null // emotionLevel이 null일 경우 보여줄 기본 이미지
        }
    }
}
