package com.egobook.app.ui.diary.mapper

import com.egobook.app.domain.model.diary.entity.DiaryType
import java.time.LocalDate

/**
 * Domain 모델과 UI 레이어 간의 데이터 변환을 담당하는 매퍼
 * 순수하게 데이터 변환만 담당하며, UI 리소스(이미지, 색상 등)는 UI 레이어에서 처리
 */
object DiaryEntityMapper {

    // ========== Domain Entity -> UI ==========

    /**
     * Domain DiaryType Set -> UI displayTypes Set
     */
    fun domainToUiDisplayTypes(types: Set<DiaryType>): Set<String> {
        return types.map { it.displayType }.toSet()
    }
    
    /**
     * Domain DiaryType -> UI displayType("감정", "고민", "칭찬", "감사")
     */
    fun domainToUiDisplayType(diaryType: DiaryType): String {
        return diaryType.displayType
    }

    // ========== UI -> Domain Entity ==========
    
    /**
     * UI displayTypes Set -> Domain DiaryType Set
     */
    fun uiDisplayTypesToDomain(displayTypes: Set<String>): Set<DiaryType> {
        return displayTypes.mapNotNull { displayType ->
            try {
                DiaryType.fromDisplayType(displayType)
            } catch (e: IllegalArgumentException) {
                null // 알 수 없는 타입은 무시
            }
        }.toSet()
    }

    /**
     * UI displayType("감정", "고민", "칭찬", "감사") -> Domain DiaryType
     */
    fun uiDisplayTypeToDomain(displayType: String): DiaryType {
        return DiaryType.fromDisplayType(displayType)
    }

    /**
     * UI 년원일 -> Domain Entity LocalDate
     */
    fun uiYearMonthDateToDomain(year: Int, month: Int, date: Int): LocalDate {
        return LocalDate.of(year, month, date)
    }
    
    // ========== EmotionLevel 변환 ==========
    // Domain과 UI 모두 Int (1~5)를 사용 -> 변환 불필요
}
