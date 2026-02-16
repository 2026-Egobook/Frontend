package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetterItem
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.google.gson.annotations.SerializedName

data class ArrivedPendingLetterResponse(
    @SerializedName("letter")
    val letter: ArrivedPendingLetterItemResponse? = null
)

data class ArrivedPendingLetterItemResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("fromLabel")
    val fromLabel: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("arrivedAt")
    val arrivedAt: String,
    @SerializedName("replyDeadlineAt")
    val replyDeadlineAt: String,
)

fun ArrivedPendingLetterResponse.toDomain(): ArrivedPendingLetter = ArrivedPendingLetter(
    letter = letter?.toDomain()
)

fun ArrivedPendingLetterItemResponse.toDomain(): ArrivedPendingLetterItem = ArrivedPendingLetterItem(
    letterId = letterId,
    status = status,
    mode = mode,
    fromLabel = fromLabel,
    content = content,
    arrivedAt = arrivedAt,
    replyDeadlineAt = replyDeadlineAt
)
