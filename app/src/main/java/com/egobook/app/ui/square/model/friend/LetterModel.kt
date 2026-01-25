package com.egobook.app.ui.square.model.friend

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LetterModel(
    val id: Int,
    val dateTime: String,
    val sentContent: String,
    val receivedContent: ReceivedModel
): Parcelable

@Parcelize
data class ReceivedModel(
    val senderNickname: String,
    val receiverNickname: String,
    val letterContent: String
): Parcelable
