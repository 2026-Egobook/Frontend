package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.LetterMode
import com.google.gson.annotations.SerializedName

data class SendLetterResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("threadId")
    val threadId: Long,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("createdAt")
    val createdAt: String
)
