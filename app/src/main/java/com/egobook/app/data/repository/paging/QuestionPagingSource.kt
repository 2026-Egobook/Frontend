package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem

class QuestionPagingSource(private val apiService: QuestionApiService): PagingSource<Int, MyTodayQuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, MyTodayQuestionAnswerItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize
            val result = apiService.fetchMyRepliesHistory(page = page, size = size).data
            LoadResult.Page(
                data = result.content.map { it.toDomain() },
                prevKey = null,
                nextKey = result.page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}