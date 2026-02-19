package com.egobook.app.domain.model.square.letter

data class ReceivedReplies(
    val content: List<ReceivedReply>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class ReceivedReply(
    val letterId: Long,
    val replyId: Long,
    val threadId: Long,
    val replyContent: String,
    val repliedAt: String,
    val isAIGenerated: Boolean,
    val isReported: Boolean,
    val mode: LetterMode,
    val fromLabel: String,
    val letterColor: LetterBackgroundColor
)
