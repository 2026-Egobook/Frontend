package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.DeferredLetter
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus

data class DeferredLetterModel(
    val letterId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val fromLabel: String,
    val backgroundColor: LetterBackgroundColor,
    val contentPreview: String,
    val arrivedAt: String,
    val replyDeadlineAt: String
)

fun DeferredLetter.toPresentation(): DeferredLetterModel = DeferredLetterModel(
    letterId = letterId,
    status = status,
    mode = mode,
    fromLabel = fromLabel,
    backgroundColor = backgroundColor,
    contentPreview = contentPreview,
    arrivedAt = arrivedAt,
    replyDeadlineAt = replyDeadlineAt
)
