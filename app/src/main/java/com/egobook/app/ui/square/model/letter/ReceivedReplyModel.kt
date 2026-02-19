package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.ReceivedReplies
import com.egobook.app.domain.model.square.letter.ReceivedReply


data class ReceivedRepliesModel(
    val content: List<ReceivedReplyModel>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)
data class ReceivedReplyModel(
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

fun ReceivedReplies.toPresentation() = ReceivedRepliesModel(
    content = content.map { it.toPresentation() },
    page = page,
    size = size,
    hasNext = hasNext
)

fun ReceivedReply.toPresentation() = ReceivedReplyModel(
    letterId = letterId,
    replyId = replyId,
    threadId = threadId,
    replyContent = replyContent,
    repliedAt = repliedAt,
    isAIGenerated = isAIGenerated,
    isReported = isReported,
    mode = mode,
    fromLabel = fromLabel,
    letterColor = letterColor
)
