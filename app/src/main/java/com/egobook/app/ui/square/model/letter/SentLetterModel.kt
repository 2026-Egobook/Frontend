package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.SentLetterItem

data class SentLetterModel(
    val letterId: Long,
    val mode: LetterMode,
    val status: LetterStatus,
    val aiReplaceAt: String,
    val content: String,
    val createdAt: String
)

fun SentLetterItem.toPresentation() = SentLetterModel(
    letterId = letterId,
    mode = mode,
    status = status,
    aiReplaceAt = aiReplaceAt,
    content = content,
    createdAt = createdAt
)