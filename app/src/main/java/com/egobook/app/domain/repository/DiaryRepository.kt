package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface DiaryRepository {

    fun getDiaries(): Flow<List<Diary>>

    suspend fun getDiaryById(id: Long): Result<Diary?>

    suspend fun addDiary(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int?, // 1~5 사이의 감정 레벨
        createdAt: LocalDateTime // 일기가 귀속될 날짜
    ): Result<Diary>

    suspend fun deleteDiaryById(id: Long): Result<Unit>
}