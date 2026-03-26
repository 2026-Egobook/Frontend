package com.egobook.app.data.repository.diary

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.DiaryApiService
import com.egobook.app.data.model.diary.response.DiaryExportResponse
import com.egobook.app.data.repository.diary.paging.DiariesPagingSource
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryDownloadForm
import com.egobook.app.domain.model.diary.entity.DiaryExportForm
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiaryRewards
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryCreateRequest
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryDownloadForm
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryEntity
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryExportRequest
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryRewardsEntity
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryUpdateRequest
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toRequestParams
import com.egobook.app.domain.repository.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton //dailyCountCache가 계속 살아있게 하기 위해 추가
class DiaryRepositoryImpl  @Inject constructor(
    private val apiService: DiaryApiService
) : DiaryRepository {

    // 날짜별 dailyCount 캐시
    private val dailyCountCache = mutableMapOf<LocalDate, Int>()

    override fun getDiaries(
        filter: DiaryFilter,
        size: Int
    ): Flow<PagingData<DiarySummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DiariesPagingSource(
                    apiService = apiService,
                    filter = filter,
                    onDailyCountReceived = { count ->
                        // API 응답 시 dailyCount 캐시 업데이트
                        Timber.d("[DailyCount] PagingSource 캐시 업데이트: date=${filter.date}, count=$count")
                        dailyCountCache[filter.date] = count
                    }
                )
            }
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

    override suspend fun addDiary(diary: Diary): Result<DiaryRewards> {
        return safeApiCall(
            apiCall = {
                apiService.addDiary(
                    diary.toDiaryCreateRequest()
                )
            },
            transform = { it.toDiaryRewardsEntity() }
        )
    }

    override suspend fun updateDiary(
        diaryId: Long,
        diary: Diary
    ): Result<Unit> {
        return safeApiCall(
            apiCall = {
                apiService.updateDiary(
                    diaryId = diaryId,
                    request = diary.toDiaryUpdateRequest()
                )
            },
            transform = { Unit }
        )
    }

    override suspend fun deleteDiaryById(diaryId: Long): Result<Unit> {
        return safeApiCall (
            apiCall = {
                apiService.deleteDiary(diaryId)
            },
            transform = { Unit }
        )
    }

    override suspend fun getDailyCount(date: LocalDate): Result<Int> {
        // 1. 캐시 먼저 확인
        dailyCountCache[date]?.let {
            Timber.d("[DailyCount] 캐시 히트: date=$date, count=$it")
            return Result.success(it)
        }

        Timber.d("[DailyCount] 캐시 미스: date=$date, API 호출 시작")

        // 2. 캐시 miss면 API 호출
        val filter = DiaryFilter(date, null)
        val (dateParam, typesParam) = filter.toRequestParams()

        return safeApiCall(
            apiCall = {
                apiService.getDiaries(
                    date = dateParam,
                    type = typesParam,
                    page = 1,
                    size = 1  // dailyCount만 필요하므로 최소 크기로 요청
                )
            },
            transform = { response ->
                // API 응답 시 캐시 저장
                dailyCountCache[date] = response.dailyCount
                Timber.d("[DailyCount] API 응답 캐시 저장: date=$date, count=${response.dailyCount}")
                response.dailyCount
            }
        )
    }

    override suspend fun exportDiary(diaryExportForm: DiaryExportForm): Result<DiaryDownloadForm> {
        return safeApiCall(
            apiCall = {
                apiService.exportDiary(
                    diaryExportForm.toDiaryExportRequest()
                )
            },
            transform = {
                it.toDiaryDownloadForm()
            }
        )
    }

}