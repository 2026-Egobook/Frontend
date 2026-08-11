package com.egobook.app.ui.home.repository

import com.egobook.app.di.qualifier.BackendApi
import com.egobook.app.domain.model.notice.Notice
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

interface NoticeRepository {
    /**
     * 현재 노출 중인 공지 중 가장 최근 1건을 조회한다.
     *
     * 조회와 동시에 서버에서 읽음 처리되어 `/home`의 `hasUnreadNotice`가 false가 된다.
     * 발행된 공지가 없으면 null을 반환한다.
     */
    suspend fun loadLatestNotice(): Notice?
}

data class NoticeDto(
    val noticeId: Int,
    val title: String,
    val notionUrl: String?,
    val publishedAt: String
) {
    fun toDomain(): Notice = Notice(id = noticeId, title = title, url = notionUrl)
}

interface NetworkNoticeService {
    @GET("/notices/latest")
    suspend fun loadLatestNotice(): BaseResponse<NoticeDto?>
}

@Singleton
class NetworkNoticeRepository @Inject constructor(
    @BackendApi private val retrofit: Retrofit
) : NoticeRepository {
    private val noticeService by lazy { retrofit.create(NetworkNoticeService::class.java) }

    override suspend fun loadLatestNotice(): Notice? = noticeService.loadLatestNotice().data?.toDomain()
}
