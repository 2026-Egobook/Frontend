package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.domain.model.counseling.PraiseDailyItem

class DailyPraisePagingSource(private val apiService: CounselingApiService) :
    PagingSource<Int, PraiseDailyItem>() {
    override fun getRefreshKey(state: PagingState<Int, PraiseDailyItem>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PraiseDailyItem> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize
            val result = apiService.fetchDailyPraise(page = page, size = size).data
            LoadResult.Page(
                data = result.content.map { it.toDomain() },
                prevKey = if(result.page == 1) null else result.page - 1,
                nextKey = if(result.hasNext) result.page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}