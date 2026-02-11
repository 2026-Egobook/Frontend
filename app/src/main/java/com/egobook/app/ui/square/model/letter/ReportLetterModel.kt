package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.ReportLetter
import com.egobook.app.domain.model.square.letter.ReportLetterType

data class ReportLetterModel(
    val reason: ReportLetterType,
    val description: String? = null
)

fun ReportLetterModel.toDomain(): ReportLetter = ReportLetter(
    reason = reason,
    description = description
)


