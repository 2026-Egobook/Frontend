package com.example.egobook.ui.counseling.model

import com.example.egobook.domain.model.PraiseMessage

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

