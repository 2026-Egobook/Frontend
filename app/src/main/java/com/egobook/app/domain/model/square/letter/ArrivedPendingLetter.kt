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
    val letterColor: LetterBackgroundColor, // TODO: 백엔드한테 필드 넣어달라고 하기
    val arrivedAt: String,
    val replyDeadlineAt: String
)