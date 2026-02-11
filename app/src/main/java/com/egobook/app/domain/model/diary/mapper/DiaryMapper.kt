package com.egobook.app.domain.model.diary.mapper

import com.egobook.app.data.model.diary.request.DiaryCreateRequest
import com.egobook.app.data.model.diary.request.DiaryUpdateRequest
import com.egobook.app.data.model.diary.response.DiariesResponse
import com.egobook.app.data.model.diary.response.DiaryEntryResponse
import com.egobook.app.data.model.diary.response.DiarySlice
import com.egobook.app.domain.model.diary.entity.DayDiaries
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiaryList
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Data Layer ↔ Domain Layer 변환 Mapper
 */
object DiaryMapper {
    
    private val KST: ZoneId = ZoneId.of("Asia/Seoul")

    // ========== Response → Domain Entity ==========

    /**
     * DiariesResponse → DayDiaries
     */
    fun DiariesResponse.toDayDiariesEntity(): DayDiaries {
        return DayDiaries(
            dailyCount = dailyCount,
            diaries = diaries.toDiaryListEntity()
        )
    }

    /**
     * DiaryEntryResponse → Diary
     */
    fun DiaryEntryResponse.toDiaryEntity(): Diary {
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
     * UTC 시간 문자열을 KST LocalDateTime으로 변환
     * @param utcString ISO 8601 UTC 형식 (예: "2026-02-08T16:03:07.148Z")
     * @return KST LocalDateTime
     */
    private fun parseUtcToKst(utcString: String): LocalDateTime {
        val withZ = if (utcString.endsWith("Z")) utcString else "${utcString}Z"
        return Instant.parse(withZ)
            .atZone(KST)
            .toLocalDateTime()
    }


    /**
     * DiarySlice → DiaryList
     */
    fun DiarySlice.toDiaryListEntity(): DiaryList {
        return DiaryList(
            content = content.map { it.toDiaryEntity().toDiarySummary() },
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

    /**
     * DiaryFilter → RequestParams
     */

    fun DiaryFilter.toRequestParams(): Pair<String, String> {
        val dateParam = date.toString()
        val typesParam = types?.joinToString(",") { it.name } ?: ""

        return dateParam to typesParam
    }

    /**
     * Diary → DiaryCreateRequest
     */

    fun Diary.toDiaryCreateRequest(): DiaryCreateRequest {
        return DiaryCreateRequest(
            type = types.map { it.value },
            emotionLevel = emotionLevel,
            content = content,
            date = date.toString()
        )
    }

    /**
     * Diary → DiaryUpdateRequest
     */
    fun Diary.toDiaryUpdateRequest(): DiaryUpdateRequest {
        return DiaryUpdateRequest(
            type = types.map { it.value },
            emotionLevel = emotionLevel,
            content = content
        )
    }

}