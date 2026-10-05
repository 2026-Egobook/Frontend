package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.google.gson.annotations.SerializedName

data class ReplyLetterRequest(
    @SerializedName("text") val text: String,
    @SerializedName("backgroundColor") val backgroundColor: LetterBackgroundColor
)
