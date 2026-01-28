package com.egobook.app.ui.diary.mapper

import androidx.annotation.DrawableRes
import com.egobook.app.R
import com.egobook.app.data.model.response.DiaryItemResponse
import com.egobook.app.data.model.response.GetDiaryResponse
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.model.EmotionLevel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DiaryMapper {

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    // ========== UI <-> Domain 변환 ==========
    
    /**
     * UI displayType("감정", "고민", "칭찬", "감사") -> Domain DiaryType
     */
    fun uiDisplayTypeToDomain(displayType: String): DiaryType {
        return DiaryType.fromDisplayType(displayType)
    }
    
    /**
     * Domain DiaryType -> UI displayType("감정", "고민", "칭찬", "감사")
     */
    fun domainToUiDisplayType(diaryType: DiaryType): String {
        return diaryType.displayType
    }
    
    /**
     * UI displayTypes 리스트 -> Domain DiaryType Set
     */
    fun uiDisplayTypesToDomain(displayTypes: List<String>): Set<DiaryType> {
        return displayTypes.mapNotNull { displayType ->
            try {
                DiaryType.fromDisplayType(displayType)
            } catch (e: IllegalArgumentException) {
                null // 알 수 없는 타입은 무시
            }
        }.toSet()
    }
    
    /**
     * Domain DiaryType Set -> UI displayTypes 리스트
     */
    fun domainToUiDisplayTypes(types: Set<DiaryType>): List<String> {
        return types.map { it.displayType }
    }
    
    /**
     * UI 감정 레벨 (1~5) -> Domain EmotionLevel
     */
    fun uiEmotionLevelToDomain(level: Int): EmotionLevel {
        return EmotionLevel.fromDisplayLevel(level)
    }
    
    /**
     * Domain EmotionLevel -> UI 감정 레벨 (1~5)
     */
    fun domainToUiEmotionLevel(emotionLevel: EmotionLevel): Int {
        return emotionLevel.displayEmotionLevel
    }
    
    /**
     * Domain EmotionLevel -> UI 감정 이미지 리소스
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
    
    /**
     * UI 감정 이미지 인덱스 (1~5) -> Domain EmotionLevel
     * (이미지 버튼 클릭 시 사용)
     */
    fun uiEmotionImageIndexToDomain(imageIndex: Int): EmotionLevel {
        return when (imageIndex) {
            1 -> EmotionLevel.VERY_BAD
            2 -> EmotionLevel.BAD
            3 -> EmotionLevel.NORMAL
            4 -> EmotionLevel.GOOD
            5 -> EmotionLevel.VERY_GOOD
            else -> throw IllegalArgumentException("Invalid emotion image index: $imageIndex. Must be 1-5")
        }
    }
}
