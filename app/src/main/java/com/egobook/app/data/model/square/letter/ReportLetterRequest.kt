package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.ReportLetter
import com.egobook.app.domain.model.square.letter.ReportLetterType
import com.google.gson.annotations.SerializedName

data class ReportLetterRequest(
    @SerializedName("reason")
    val reason: ReportLetterType,
    @SerializedName("description")
    val description: String? = null
)

fun ReportLetter.toData(): ReportLetterRequest = ReportLetterRequest(
    reason = reason,
    description = description
)