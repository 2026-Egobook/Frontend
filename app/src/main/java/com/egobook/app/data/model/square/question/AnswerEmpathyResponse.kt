package com.egobook.app.data.model.square.question

import com.egobook.app.domain.model.square.question.AnswerEmpathy
import com.google.gson.annotations.SerializedName

data class AnswerEmpathyResponse(
    @SerializedName("answerId")
    val answerId: Long,
    @SerializedName("empathized")
    val empathized: Boolean,
    @SerializedName("empathyCount")
    val empathyCount: Long
)

fun AnswerEmpathyResponse.toDomain(): AnswerEmpathy =
    AnswerEmpathy(
        answerId = answerId,
        empathized = empathized,
        empathyCount = empathyCount
    )
