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
            val mockDailyPraises = listOf(
                DailyPraise(
                    id = 1,
                    diaryDate = "2024-02-03",
                    isRead = true
                ),
                DailyPraise(
                    id = 2,
                    diaryDate = "2024-02-05",
                    isRead = true
                ),
                DailyPraise(
                    id = 3,
                    diaryDate = "2024-02-06",
                    isRead = true
                ),
                DailyPraise(
                    id = 4,
                    diaryDate = "2024-02-09",
                    isRead = true
                ),
                DailyPraise(
                    id = 5,
                    diaryDate = "2024-02-11",
                    isRead = true
                ),
                DailyPraise(
                    id = 6,
                    diaryDate = "2024-02-13",
                    isRead = true
                ),
                DailyPraise(
                    id = 7,
                    diaryDate = "2024-02-14",
                    isRead = false
                ),
                DailyPraise(
                    id = 8,
                    diaryDate = "2024-02-16",
                    isRead = false
                ),
                DailyPraise(
                    id = 9,
                    diaryDate = "2024-02-18",
                    isRead = false
                )
            )
            LoadResult.Page(
                data = mockDailyPraises,
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