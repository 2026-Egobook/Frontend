package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import kotlinx.coroutines.delay

/**
 * 1. "새로고침(Refresh) 이벤트 발생시 반환하는 페이지부터 다시 읽어라"는 의미
 * 2. 첫 로딩 시에 key 값이 null로 내려온다.
 */
class MyRepliesHistoryPagingSource(private val apiService: QuestionApiService): PagingSource<Int, MyTodayQuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, MyTodayQuestionAnswerItem>): Int {
        return 1 // 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: 1 // 2
            val size = params.loadSize

//            val result = apiService.fetchMyRepliesHistory(page = page, size = size).data
//            LoadResult.Page(
//                data = result.content.map { it.toDomain() },
//                prevKey = if(result.page == 1) null else result.page - 1,
//                nextKey = if(result.hasNext) result.page + 1 else null
//            )

            if(page > 1) delay(1000)

            val mockContent = List(size) { index ->
                MyTodayQuestionAnswerItem(
                    questionId = (page * size + index).toLong(),
                    questionDate = "2026-01-28",
                    questionContent = "[$page 페이지] 질문 내용입니다.",
                    answerId = (page * size + index).toLong(),
                    answerContent = "답변 내용입니다.",
                    visibility = AnswerVisibility.PUBLIC,
                    answeredAt = "2026-01-28T07:30:00Z"
                )
            }
            val hasNext = page < 5
            LoadResult.Page(
                data = mockContent,
                prevKey = if(page == 1) null else page - 1,
                nextKey = if(hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}