package com.egobook.app.data.model.square.letter

import com.google.gson.annotations.SerializedName

data class ReplyLetterRequest(
    @SerializedName("text") val text: String
)
