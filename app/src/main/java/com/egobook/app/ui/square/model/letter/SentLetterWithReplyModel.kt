package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterReply
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.SentLetterWithReply

data class SentLetterWithReplyModel(
    val letterId: Long,
    val threadId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val sentContent: String,
    val backgroundColor: LetterBackgroundColor,
    val createdAt: String,
    val arrivedAt: String,
    val reply: LetterReplyModel? = null
)

data class LetterReplyModel(
    val replyId: Long,
    val replyContent: String,
    val isAIGenerated: Boolean,
    val isReported: Boolean,
    val repliedAt: String
)

fun SentLetterWithReply.toPresentation() = SentLetterWithReplyModel(
    letterId = letterId,
    threadId = threadId,
    status = status,
    mode = mode,
    sentContent = sentContent,
    backgroundColor = backgroundColor,
    createdAt = createdAt,
    arrivedAt = arrivedAt,
    reply = reply?.toPresentation()
)

fun LetterReply.toPresentation() = LetterReplyModel(
    replyId = replyId,
    replyContent = replyContent,
    isAIGenerated = isAIGenerated,
    isReported = isReported,
    repliedAt = repliedAt
)

