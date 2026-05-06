package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.domain.model.counseling.DailyPraise

class DailyPraisePagingSource(private val apiService: CounselingApiService) :
    PagingSource<Int, DailyPraise>() {
    override fun getRefreshKey(state: PagingState<Int, DailyPraise>): Int {
        return FIRST_PAGE_NUM
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DailyPraise> {
        return try {
            val page = params.key ?: FIRST_PAGE_NUM
            val size = params.loadSize
            val result = apiService.fetchDailyPraises(page = page, size = size).data
            LoadResult.Page(
                data = result.content.map { it.toDomain() },
                prevKey = if(page == FIRST_PAGE_NUM) null else page - 1,
                nextKey = if(result.hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE_NUM = 1
    }
}
