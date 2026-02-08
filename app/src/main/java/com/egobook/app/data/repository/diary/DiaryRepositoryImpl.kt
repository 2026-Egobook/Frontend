package com.egobook.app.data.repository.diary

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.DiaryApiService
import com.egobook.app.data.model.diary.request.DiaryCreateRequest
import com.egobook.app.data.repository.diary.paging.DiariesPagingSource
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryCreateRequest
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryEntity
import com.egobook.app.domain.repository.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class DiaryRepositoryImpl  @Inject constructor(
    private val apiService: DiaryApiService
) : DiaryRepository {
    override fun getDiaries(
        filter: DiaryFilter,
        size: Int
    ): Flow<PagingData<DiarySummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DiariesPagingSource(apiService, filter) }
        ).flow
    }

    override suspend fun getDiaryById(diaryId: Long): Result<Diary> {
        return safeApiCall(
            apiCall = {
                apiService.getDiary(diaryId)
            },
            transform = {it.toDiaryEntity()}
        )
    }

    override suspend fun addDiary(diary: Diary): Result<Unit> {
        return safeApiCall(
            apiCall = {
                apiService.addDiary(
                    diary.toDiaryCreateRequest()
                )
            },
            transform = { Unit }
        )
    }

    override suspend fun updateDiary(
        diaryId: Long,
        types: Set<DiaryType>,
        emotionLevel: Int?,
        content: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDiaryById(diaryId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

}