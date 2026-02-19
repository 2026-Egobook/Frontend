package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.domain.model.square.letter.DeferredLetter

class DeferredLettersPagingSource(private val apiService: LetterApiService): PagingSource<Int, DeferredLetter>() {
    override fun getRefreshKey(state: PagingState<Int, DeferredLetter>): Int {
        return FIRST_PAGE_NUM
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DeferredLetter> {
        return try {
            val page = params.key ?: FIRST_PAGE_NUM
            val size = params.loadSize
            val result = apiService.fetchDeferredLetters(page = page, size = size).data
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