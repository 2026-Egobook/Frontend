package com.egobook.app.domain.model.square.letter

data class SentLetter(
    val content: List<SentLetterItem>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class SentLetterItem(
    val letterId: Long,
    val mode: LetterMode,
    val status: LetterStatus,
    val aiReplaceAt: String,
    val content: String,
    val createdAt: String
)