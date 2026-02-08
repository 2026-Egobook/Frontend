package com.egobook.app.data.repository.diary.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.DiaryApiService
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiaryEntity
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toRequestParams
import com.egobook.app.domain.model.diary.mapper.DiaryMapper.toDiarySummary


class DiariesPagingSource(
    private val apiService: DiaryApiService,
    private val filter: DiaryFilter
): PagingSource<Int, DiarySummary>() {

    override fun getRefreshKey(state: PagingState<Int, DiarySummary>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiarySummary> {
        val page = params.key ?: 1 // api가 보내주는 디폴트값이 1이어서
        val size = params.loadSize

        val (dateParam, typesParam) = filter.toRequestParams()

        //응답 받은거
        val response = apiService.getDiaries(
            date = dateParam,
            type = typesParam,
            page = page,
            size = size
        )

        val diarySlice = response.data.diaries


        return LoadResult.Page(
            data = diarySlice.content.map { it.toDiaryEntity().toDiarySummary() },
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (diarySlice.hasNext) page + 1 else null
        )
    }

}