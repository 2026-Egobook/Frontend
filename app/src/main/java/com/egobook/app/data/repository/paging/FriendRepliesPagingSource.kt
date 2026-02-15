package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import kotlinx.coroutines.delay

class FriendRepliesPagingSource(private val apiService: QuestionApiService) :
    PagingSource<Int, UserTodayQuestionAnswerItem>() {
    override fun getRefreshKey(state: PagingState<Int, UserTodayQuestionAnswerItem>): Int {
        return FIRST_PAGE_NUM
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_NUM
            val size = params.loadSize
            val result = apiService.fetchTodayFriendReplies(page = page, size = size).data
            LoadResult.Page(
                data = result.content.map { it.toDomain() },
                prevKey = if(result.page == FIRST_PAGE_NUM) null else result.page - 1,
                nextKey = if(result.hasNext) result.page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private val FIRST_PAGE_NUM = 1
    }
}