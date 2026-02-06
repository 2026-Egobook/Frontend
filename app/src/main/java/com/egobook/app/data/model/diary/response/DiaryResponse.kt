package com.egobook.app.data.model.diary.response

data class DiaryResponse (
    val diaryId: Long,

    val date: String,

    val writtenAt: String,

    val types: List<String>,

    val emotionLevel: Long?,

    val content: String,

    val createdAt: String,

    val updatedAt: String

)