package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import kotlinx.coroutines.delay

class AllUserRepliesPagingSource(private val apiService: QuestionApiService): PagingSource<Int, UserTodayQuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, UserTodayQuestionAnswerItem>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize
//            val result = apiService.fetchTodayAllUserReplies(page = page, size = size).data
//            LoadResult.Page(
//                data = result.content.map { it.toDomain() },
//                prevKey = if(result.page == 1) null else result.page - 1,
//                nextKey = if(result.hasNext) result.page + 1 else null
//            )
            if(page > 1) delay(1000)
            val mockData = List(size) { index ->
                UserTodayQuestionAnswerItem(
                    answerId = (page * size + index).toLong(),
                    userId = (page * size + index).toLong(),
                    nickname = "다른 유저${page*size+index}",
                    content = "다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리다른 유저의 답이 보이는 자리다른 유저의 답이 보이는 자리다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리 다른 유저의 답이 보이는 자리",
                    createdAt = "2026-01-28T07:30:00Z"
                )
            }
            val hasNext = page < 5
            LoadResult.Page(
                data = mockData,
                prevKey = if(page == 1) null else page - 1,
                nextKey = if(hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}