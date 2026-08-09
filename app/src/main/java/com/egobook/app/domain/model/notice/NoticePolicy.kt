package com.egobook.app.domain.model.notice

sealed interface NoticeOpenResult {
    data class Success(val url: String) : NoticeOpenResult
    data object Failure : NoticeOpenResult
}

object NoticePolicy {
    private val ALLOWED_SCHEMES = listOf("https://", "http://")

    fun resolve(url: String?): NoticeOpenResult {
        val trimmed = url?.trim().orEmpty()
        if (ALLOWED_SCHEMES.none { trimmed.startsWith(it, ignoreCase = true) }) {
            return NoticeOpenResult.Failure
        }
        return NoticeOpenResult.Success(trimmed)
    }
}
