package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.toDomain
import com.egobook.app.domain.model.square.letter.SentLetterItem

class SentLettersPagingSource(private val apiService: LetterApiService): PagingSource<Int, SentLetterItem>() {
    override fun getRefreshKey(state: PagingState<Int, SentLetterItem>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SentLetterItem> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize
            val data = apiService.fetchSentLetters(page = page, size = size).data
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