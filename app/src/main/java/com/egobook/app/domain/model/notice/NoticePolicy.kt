package com.egobook.app.domain.model.notice

sealed interface NoticeOpenResult {
    data class Success(val url: String) : NoticeOpenResult
    data object Failure : NoticeOpenResult
}

/** 공지를 웹뷰로 열 수 있는지 판단하는 정책. */
object NoticePolicy {
    private val ALLOWED_SCHEMES = listOf("https://", "http://")

    fun resolve(notice: Notice?): NoticeOpenResult {
        val trimmed = notice?.url?.trim().orEmpty()
        if (ALLOWED_SCHEMES.none { trimmed.startsWith(it, ignoreCase = true) }) {
            return NoticeOpenResult.Failure
        }
        return NoticeOpenResult.Success(trimmed)
    }
}
