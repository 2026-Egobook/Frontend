package com.egobook.app.ui.diary.mapper

import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryType
import java.time.LocalDate
import java.time.LocalDateTime

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

    /**
     * UI 상태를 Domain Diary 엔티티로 변환 (새 일기 생성용)
     * @param selectedTypes UI displayType Set (예: ["감정", "고민"])
     * @param content 일기 내용
     * @param emotionLevel 감정 레벨 (1~5)
     * @param dateTime 선택된 날짜+시간
     * @return 새로 생성할 Diary 엔티티 (diaryId와 createdAt는 임시값)
     */
    fun createNewDiary(
        selectedTypes: Set<String>,
        content: String,
        emotionLevel: Int?,
        dateTime: LocalDateTime
    ): Diary {
        // UI displayType을 Domain DiaryType으로 변환
        val diaryTypes = uiDisplayTypesToDomain(selectedTypes)
        
        // 감정 타입이 선택되지 않았으면 emotionLevel은 null
        val finalEmotionLevel = if (selectedTypes.contains("감정")) {
            emotionLevel
        } else {
            null
        }
        
        return Diary(
            diaryId = 0L, // 새 일기는 임시 ID (서버가 생성), 임시 삽입.
            date = dateTime.toLocalDate(),
            writtenAt = dateTime,
            types = diaryTypes,
            emotionLevel = finalEmotionLevel,
            content = content,
            createdAt = dateTime // 서버가 실제 값으로 대체. 임시 삽입
        )
    }
}
