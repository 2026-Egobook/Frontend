package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.ArrivedPendingLetter
import com.egobook.app.domain.model.square.letter.ArrivedPendingLetterItem
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.ui.square.model.letter.LetterBackgroundColor
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
    val fromLabel: String, // 무슨 필드인지 의미 잘 모르겠음
    @SerializedName("content")
    val content: String,
    @SerializedName("arrivedAt")
    val arrivedAt: String,
    @SerializedName("replyDeadlineAt")
    val replyDeadlineAt: String,
    @SerializedName("letterColor")
    val letterColor: LetterBackgroundColor // TODO: 백엔드한테 필드 넣어달라고 하기
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
    replyDeadlineAt = replyDeadlineAt,
    letterColor = letterColor
)
