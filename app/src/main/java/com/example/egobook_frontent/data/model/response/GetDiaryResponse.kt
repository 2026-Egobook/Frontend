package com.example.egobook_frontent.data.model.response

import java.time.LocalDate

data class GetDiaryResponse (
    val diaryId: Long,

    val date: String,

    val writtenAt: String,

    val types: List<String>,

    val emotionLevel: Long?,

    val content: String,

    val createdAt: String,

    val updatedAt: String

)


