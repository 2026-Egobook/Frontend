package com.egobook.app.domain.repository.diary

import androidx.paging.PagingData
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface DiaryRepository {
    fun getDiaries(
        filter: DiaryFilter,
        size: Int = 10
    ): Flow<PagingData<DiarySummary>>

    /**
     * 일기 상세 조회
     * @param diaryId 일기 ID
     */
    suspend fun getDiaryById(diaryId: Long): Result<Diary>

    /**
     * 일기 생성
     */
    suspend fun addDiary(diary: Diary): Result<Unit>

    /**
     * 일기 수정
     */
    suspend fun updateDiary(
        diaryId: Long,
        diary: Diary
    ): Result<Unit>

    /**
     * 일기 삭제
     */
    suspend fun deleteDiaryById(diaryId: Long): Result<Unit>
}