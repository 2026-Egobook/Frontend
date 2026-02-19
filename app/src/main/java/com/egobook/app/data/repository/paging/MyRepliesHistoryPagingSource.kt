package com.egobook.app.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import kotlinx.coroutines.delay

/**
 * 1. "새로고침(Refresh) 이벤트 발생시 반환하는 페이지부터 다시 읽어라"는 의미
 * 2. 첫 로딩 시에 key 값이 null로 내려온다.
 * PagingSource에 들어가는 key 값은 데이터의 페이지 번호를 나타낸다.
 * PagingSource<Int, Item> = 정수 형태의 페이지 번호를 줄 테니, 해당 페이지의 아이템들을 가져오라는 의미
 * params.key가 우리가 정의한 'Int' 타입의 키이다
 */
class MyRepliesHistoryPagingSource(private val apiService: QuestionApiService): PagingSource<Int, MyTodayQuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, MyTodayQuestionAnswerItem>): Int {
        return FIRST_PAGE_NUM // 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyTodayQuestionAnswerItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_NUM // 2
            val size = params.loadSize

            val result = apiService.fetchMyRepliesHistory(page = page, size = size).data

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