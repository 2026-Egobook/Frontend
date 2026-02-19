package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.ReceivedReplies
import com.egobook.app.domain.model.square.letter.ReceivedReply
import com.google.gson.annotations.SerializedName

data class ReceivedRepliesResponse(
    @SerializedName("content")
    val content: List<ReceivedReplyResponse>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class ReceivedReplyResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("replyId")
    val replyId: Long,
    @SerializedName("threadId")
    val threadId: Long,
    @SerializedName("replyText")
    val replyContent: String,
    @SerializedName("repliedAt")
    val repliedAt: String,
    @SerializedName("aiGenerated")
    val isAIGenerated: Boolean,
    @SerializedName("reported")
    val isReported: Boolean,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("fromLabel")
    val fromLabel: String,
    @SerializedName("backgroundColor")
    val letterColor: LetterBackgroundColor
)

fun ReceivedRepliesResponse.toDomain() = ReceivedReplies(
    content = content.map { it.toDomain() },
    page = page,
    size = size,
    hasNext = hasNext
)

fun ReceivedReplyResponse.toDomain(): ReceivedReply = ReceivedReply(
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
