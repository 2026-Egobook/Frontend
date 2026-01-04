package com.example.egobook_frontent.data.model

import com.example.egobook_frontent.domain.model.PraiseMessage

data class PraiseMessageResponse(
    val id: Int,
    val message: String,
    val createdAt: String
) {
    fun toDomain() = PraiseMessage(id = id, content = message, date = createdAt)
}
