package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterReply
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.SentLetterWithReply
import com.google.gson.annotations.SerializedName

data class SentLetterWithReplyResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("threadId")
    val threadId: Long,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("content")
    val sentContent: String,
    @SerializedName("backgroundColor")
    val backgroundColor: LetterBackgroundColor,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("arrivedAt")
    val arrivedAt: String,
    @SerializedName("reply")
    val reply: LetterReplyResponse? = null
)

data class LetterReplyResponse(
    @SerializedName("replyId")
    val replyId: Long,
    @SerializedName("text")
    val replyContent: String,
    @SerializedName("aiGenerated")
    val isAIGenerated: Boolean,
    @SerializedName("reported")
    val isReported: Boolean,
    @SerializedName("createdAt")
    val repliedAt: String
)

fun LetterReplyResponse.toDomain(): LetterReply = LetterReply(
    replyId = replyId,
    replyContent = replyContent,
    isAIGenerated = isAIGenerated,
    isReported = isReported,
    repliedAt = repliedAt
)

fun SentLetterWithReplyResponse.toDomain(): SentLetterWithReply = SentLetterWithReply(
    letterId = letterId,
    threadId = threadId,
    status = status,
    mode = mode,
    sentContent = sentContent,
    backgroundColor = backgroundColor,
    createdAt = createdAt,
    arrivedAt = arrivedAt,
    reply = reply?.toDomain()
)