package com.egobook.app.domain.model.diary.entity

import java.time.LocalDate

data class DiaryFilter(
    val date: LocalDate,
    val types: Set<DiaryType>? = null
)