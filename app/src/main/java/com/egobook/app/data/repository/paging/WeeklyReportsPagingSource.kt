package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.domain.model.counseling.WeeklyReportItem

class WeeklyReportsPagingSource(private val apiService: CounselingApiService): PagingSource<Int, WeeklyReportItem>() {
    override fun getRefreshKey(state: PagingState<Int, WeeklyReportItem>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WeeklyReportItem> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize
            val data = apiService.fetchWeeklyReports(page = page, size = size).data
            LoadResult.Page(
                data = data.content.map { it.toDomain() },
                prevKey = if(page == 1) null else page - 1,
                nextKey = if(data.hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}