package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.model.counseling.toDomain
import com.egobook.app.domain.model.counseling.WeeklyReport

class WeeklyReportsPagingSource(private val apiService: CounselingApiService): PagingSource<Int, WeeklyReport>() {
    override fun getRefreshKey(state: PagingState<Int, WeeklyReport>): Int {
        return FIRST_PAGE_NUM
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WeeklyReport> {
        return try {
            val page = params.key ?: FIRST_PAGE_NUM
            val size = params.loadSize
            val data = apiService.fetchWeeklyReports(page = page, size = size).data
            LoadResult.Page(
                data = data.content.map { it.toDomain() },
                prevKey = if(page == FIRST_PAGE_NUM) null else page - 1,
                nextKey = if(data.hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private val FIRST_PAGE_NUM = 1
    }
}