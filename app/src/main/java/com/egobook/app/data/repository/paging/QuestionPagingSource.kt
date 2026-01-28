package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import kotlinx.coroutines.delay

/**
 * 1. 첫 로딩 시에 key 값이 null로 내려온다.
 */
class QuestionPagingSource(private val apiService: QuestionApiService): PagingSource<Int, MyTodayQuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, MyTodayQuestionAnswerItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: 1 // 1
            val size = params.loadSize

//            val result = apiService.fetchMyRepliesHistory(page = page, size = size).data
//            LoadResult.Page(
//                data = result.content.map { it.toDomain() },
//                prevKey = null,
//                nextKey = result.page + 1
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