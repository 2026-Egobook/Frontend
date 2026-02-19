package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.model.square.letter.ReportLetterType
import com.google.gson.annotations.SerializedName

data class ReportContentRequest(
    @SerializedName("reason")
    val reason: ReportLetterType,
    @SerializedName("description")
    val description: String? = null
)

fun ReportContent.toData(): ReportContentRequest = ReportContentRequest(
    reason = reason,
    description = description
)