package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    fun getDiaries(): Flow<List<Diary>>

    suspend fun addNote(diary: Diary)


}