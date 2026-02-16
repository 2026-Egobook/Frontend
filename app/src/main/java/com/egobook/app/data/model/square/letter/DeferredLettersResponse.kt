package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.DeferredLetter
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.google.gson.annotations.SerializedName

data class DeferredLettersResponse(
    @SerializedName("content")
    val content: List<DeferredLetterResponse>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class DeferredLetterResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("fromLabel")
    val fromLabel: String,
    @SerializedName("backgroundColor")
    val backgroundColor: LetterBackgroundColor,
    @SerializedName("contentPreview")
    val contentPreview: String,
    @SerializedName("arrivedAt")
    val arrivedAt: String,
    @SerializedName("replyDeadlineAt")
    val replyDeadlineAt: String
)

fun DeferredLetterResponse.toDomain(): DeferredLetter = DeferredLetter(
    letterId = letterId,
    status = status,
    mode = mode,
    fromLabel = fromLabel,
    backgroundColor = backgroundColor,
    contentPreview = contentPreview,
    arrivedAt = arrivedAt,
    replyDeadlineAt = replyDeadlineAt
)
