package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetterItem
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus

data class ArrivedPendingLetterModel(
    val letter: ArrivedPendingLetterItemModel? = null
)

data class ArrivedPendingLetterItemModel(
    val letterId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val fromLabel: String,
    val content: String,
    val letterColor: LetterBackgroundColor,
    val arrivedAt: String,
    val replyDeadlineAt: String
)

fun ArrivedPendingLetter.toPresentation(): ArrivedPendingLetterModel = ArrivedPendingLetterModel(
    letter = letter?.toPresentation()
)

fun ArrivedPendingLetterItem.toPresentation(): ArrivedPendingLetterItemModel =
    ArrivedPendingLetterItemModel(
        letterId = letterId,
        status = status,
        mode = mode,
        fromLabel = fromLabel,
        content = content,
        arrivedAt = arrivedAt,
        replyDeadlineAt = replyDeadlineAt,
        letterColor = letterColor
    )
