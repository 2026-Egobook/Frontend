package com.egobook.app.domain.model.diary.mapper

import com.egobook.app.data.model.diary.response.DiariesResponse
import com.egobook.app.data.model.diary.response.DiaryEntryResponse
import com.egobook.app.data.model.diary.response.DiarySlice
import com.egobook.app.domain.model.diary.entity.DayDiaries
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryList
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import java.time.LocalDate
import java.time.LocalDateTime


/**
 * Data Layer ↔ Domain Layer 변환 Mapper
 */
object DiaryMapper {

    // ========== Response → Domain Entity ==========

    /**
     * DiariesResponse → DayDiaries
     */
    fun DiariesResponse.toEntity(): DayDiaries {
        return DayDiaries(
            dailyCount = dailyCount,
            diaries = diaries.toEntity()
        )
    }

    /**
     * DiaryEntryResponse → Diary
     */
    fun DiaryEntryResponse.toEntity(): Diary {
        return Diary(
            diaryId = diaryId,
            date = LocalDate.parse(date),
            writtenAt = LocalDateTime.parse(writtenAt),
            types = type.map { DiaryType.from(it) }.toSet(),
            emotionLevel = emotionLevel,
            content = content,
            createdAt = LocalDateTime.parse(createdAt)
        )
    }

    /**
     * DiarySlice → DiaryList
     */
    fun DiarySlice.toEntity(): DiaryList {
        return DiaryList(
            content = content.map { it.toEntity().toDiarySummary() },
            page = page,
            size = size,
            hasNext = hasNext
        )
    }

    // ========== Domain Entity → Domain Entity ==========

    /**
     * Diary → DiarySummary (도메인 엔티티 간 변환)
     */
    fun Diary.toDiarySummary(): DiarySummary {
        return DiarySummary(
            diaryId = diaryId,
            writtenAt = writtenAt,
            types = types,
            emotionLevel = emotionLevel,
            content = content
        )
    }

    // ========== Domain Entity -> Request ==========


}