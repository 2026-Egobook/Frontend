package com.egobook.app.domain.model.square.letter

data class ReportLetter(
    val reason: ReportLetterType,
    val description: String? = null
)
