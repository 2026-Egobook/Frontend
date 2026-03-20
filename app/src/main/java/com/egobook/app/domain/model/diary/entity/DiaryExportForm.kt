package com.egobook.app.domain.model.diary.entity

import java.time.LocalDate

data class DiaryExportForm(
    val format: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)