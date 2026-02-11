package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.SentLetter
import com.egobook.app.domain.model.square.letter.SentLetterItem
import com.google.gson.annotations.SerializedName

data class SentLetterResponse(
    @SerializedName("content")
    val content: List<SentLetterItemResponse>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class SentLetterItemResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("aiReplaceAt")
    val aiReplaceAt: String,
    @SerializedName("lastMessagePreview")
    val content: String,
    @SerializedName("createdAt")
    val createdAt: String
)

fun SentLetterItemResponse.toDomain() = SentLetterItem(
    letterId = letterId,
    mode = mode,
    status = status,
    aiReplaceAt = aiReplaceAt,
    content = content,
    createdAt = createdAt
)

fun SentLetterResponse.toDomain() = SentLetter(
    content = content.map { it.toDomain() },
    page = page,
    size = size,
    hasNext = hasNext
)