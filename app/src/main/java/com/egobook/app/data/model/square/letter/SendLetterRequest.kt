package com.egobook.app.data.model.square.letter

import com.egobook.app.ui.square.model.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.SendLetter
import com.google.gson.annotations.SerializedName

data class SendLetterRequest(
    @SerializedName("mode")
    val mode: LetterMode,
    @SerializedName("toFriendId")
    val receiverId: Long? = null,
    @SerializedName("text")
    val content: String,
    @SerializedName("backgroundColor")
    val letterColor: LetterBackgroundColor
)

fun SendLetter.toData(): SendLetterRequest = SendLetterRequest(
    mode = mode,
    receiverId = receiverId,
    content = content,
    letterColor = letterColor
)
