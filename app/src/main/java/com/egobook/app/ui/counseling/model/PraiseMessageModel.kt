package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.PraiseMessage

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

