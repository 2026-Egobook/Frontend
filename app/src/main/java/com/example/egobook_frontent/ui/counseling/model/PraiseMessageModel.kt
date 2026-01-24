package com.example.egobook_frontent.ui.counseling.model

import com.example.egobook_frontent.domain.model.PraiseMessage

data class PraiseMessageModel(
    val id: Int,
    val messageText: String,
    val formattedDate: String
)

fun PraiseMessage.toPresentation(): PraiseMessageModel = PraiseMessageModel(
    id = id,
    messageText = content,
    formattedDate = date
)

