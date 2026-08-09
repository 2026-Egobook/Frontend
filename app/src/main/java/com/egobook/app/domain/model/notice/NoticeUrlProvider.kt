package com.egobook.app.domain.model.notice

/**
 * 공지 페이지(노션) 주소 제공자.
 *
 * 공지 API가 아직 없으므로 항상 null을 반환하며, 이 경우 홈에서는 공지 로드 실패 다이얼로그를 표시한다.
 * API가 연결되면 이 구현을 서버 응답 기반으로 교체한다.
 */
object NoticeUrlProvider {
    fun noticeUrl(): String? = null
}
