package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.PraiseMessage

data class PraiseMessageResponse(
    val id: Int,
    val message: String,
    val createdAt: String
) {
    fun toDomain() = PraiseMessage(id = id, content = message, date = createdAt)
}
