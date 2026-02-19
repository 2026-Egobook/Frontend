package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.model.square.letter.ReportLetterType

data class ReportContentModel(
    val reason: ReportLetterType,
    val description: String? = null
)

fun ReportContentModel.toDomain(): ReportContent = ReportContent(
    reason = reason,
    description = description
)


