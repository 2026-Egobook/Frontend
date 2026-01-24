package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    fun getDiaries(): Flow<List<Diary>>

    suspend fun getDiaryById(id: Long): Result<Diary?>

    suspend fun addDiary(diary: Diary): Result<Unit>

    suspend fun deleteDiary(diary: Diary): Result<Unit>
}