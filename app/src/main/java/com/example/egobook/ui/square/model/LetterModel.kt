package com.example.egobook.ui.square.model

data class LetterModel(
    val id: Int,
    val dateTime: String,
    val sentContent: String,
    val receivedContent: ReceivedModel
)

data class ReceivedModel(
    val senderNickname: String,
    val receiverNickname: String,
    val letterContent: String
)
