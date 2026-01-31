package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.SendLetter

data class SendLetterModel(
    val mode: LetterMode,
    val receiverId: Long? = null,
    val content: String,
    val letterColor: LetterBackgroundColor
)

fun SendLetterModel.toDomain(): SendLetter = SendLetter(
    mode = mode,
    receiverId = receiverId,
    content = content,
    letterColor = letterColor
)