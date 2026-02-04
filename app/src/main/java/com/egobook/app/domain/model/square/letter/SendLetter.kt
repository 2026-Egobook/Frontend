package com.egobook.app.domain.model.square.letter

data class SendLetter(
    val mode: LetterMode,
    val receiverId: Long? = null,
    val content: String,
    val letterColor: LetterBackgroundColor
)
