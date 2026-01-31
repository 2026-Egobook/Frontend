package com.egobook.app.domain.model.square.letter

import com.egobook.app.ui.square.model.letter.LetterBackgroundColor

data class SendLetter(
    val mode: LetterMode,
    val receiverId: Long? = null,
    val content: String,
    val letterColor: LetterBackgroundColor
)
