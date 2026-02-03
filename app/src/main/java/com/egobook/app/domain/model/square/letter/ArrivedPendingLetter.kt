package com.egobook.app.domain.model.square.letter

data class ArrivedPendingLetter(
    val letter: ArrivedPendingLetterItem? = null
)

data class ArrivedPendingLetterItem(
    val letterId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val fromLabel: String,
    val content: String,
    val arrivedAt: String,
    val replyDeadlineAt: String
)