package com.egobook.app.domain.model.notice

/** 현재 노출 중인 공지 중 가장 최근 1건. */
data class Notice(
    val id: Int,
    val title: String,
    val url: String?
)
