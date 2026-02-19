package com.egobook.app.domain.model.square.letter

data class DeferredLetter(
    val letterId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val fromLabel: String,
    val backgroundColor: LetterBackgroundColor,
    val contentPreview: String,
    val arrivedAt: String,
    val replyDeadlineAt: String
)
