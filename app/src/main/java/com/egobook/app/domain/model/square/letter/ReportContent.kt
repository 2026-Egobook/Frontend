package com.egobook.app.domain.model.square.letter

data class ReportContent(
    val reason: ReportLetterType,
    val description: String? = null
)
