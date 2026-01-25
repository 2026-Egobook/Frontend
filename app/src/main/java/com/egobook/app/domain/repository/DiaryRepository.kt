package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.model.EmotionLevel
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    fun getDiaries(): Flow<List<Diary>>

    suspend fun getDiaryById(id: Long): Result<Diary?>

    suspend fun addDiary(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: EmotionLevel?
    ): Result<Diary>


    suspend fun deleteDiaryById(id: Long): Result<Unit>
}