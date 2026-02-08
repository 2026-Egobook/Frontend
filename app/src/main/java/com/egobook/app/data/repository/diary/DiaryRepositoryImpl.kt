package com.egobook.app.data.repository.diary

import androidx.paging.PagingData
import com.egobook.app.data.api.DiaryApiService
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.repository.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiaryRepositoryImpl  @Inject constructor(
    private val apiService: DiaryApiService
) : DiaryRepository {
    override fun getDiaries(
        filter: DiaryFilter,
        size: Int
    ): Flow<PagingData<DiarySummary>> {
        TODO("Not yet implemented")
    }

    override suspend fun getDiary(diaryId: Long): Result<Diary> {
        TODO("Not yet implemented")
    }

    override suspend fun addDiary(
        date: String,
        types: List<String>,
        emotionLevel: Int?,
        content: String
    ): Result<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDiary(
        diaryId: Long,
        date: String,
        types: List<String>,
        emotionLevel: Int?,
        content: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDiary(diaryId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

}