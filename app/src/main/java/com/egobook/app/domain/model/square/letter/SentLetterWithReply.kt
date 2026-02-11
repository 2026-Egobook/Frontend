package com.egobook.app.domain.model.square.letter

data class SentLetterWithReply(
    val letterId: Long,
    val threadId: Long,
    val status: LetterStatus,
    val mode: LetterMode,
    val sentContent: String,
    val backgroundColor: LetterBackgroundColor,
    val createdAt: String,
    val arrivedAt: String,
    val reply: LetterReply? = null
)

data class LetterReply(
    val replyId: Long,
    val replyContent: String,
    val isAIGenerated: Boolean,
    val isReported: Boolean,
    val repliedAt: String
)
