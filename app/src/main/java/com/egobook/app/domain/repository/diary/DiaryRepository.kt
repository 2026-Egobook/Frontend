package com.egobook.app.domain.repository.diary

import androidx.paging.PagingData
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {

    /**
     * 일기 목록 가져오기 (페이징)
     * @param date 날짜 필터 (예: "2026-02-07")
     * @param type 타입 필터 (예: "EMOTION")
     * @param size 페이지 크기
     */
    fun getDiaries(
        date: LocalDate,
        type: DiaryType,
        size: Int = 10
    ): Flow<PagingData<DiarySummary>>

    /**
     * 일기 상세 조회
     * @param diaryId 일기 ID
     */
    suspend fun getDiary(diaryId: Long): Result<Diary>

    /**
     * 일기 생성
     */
    suspend fun addDiary(
        date: String,
        types: List<String>,
        emotionLevel: Int?,
        content: String
    ): Result<Long> // diaryId 반환

    /**
     * 일기 수정
     */
    suspend fun updateDiary(
        diaryId: Long,
        date: String,
        types: List<String>,
        emotionLevel: Int?,
        content: String
    ): Result<Unit>

    /**
     * 일기 삭제
     */
    suspend fun deleteDiary(diaryId: Long): Result<Unit>
}