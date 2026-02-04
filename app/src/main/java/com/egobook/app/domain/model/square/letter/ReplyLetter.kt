package com.egobook.app.domain.model.square.letter

import com.google.gson.annotations.SerializedName

data class ReplyLetter(
    val letterId: Long,
    val status: LetterStatus,
    val repliedAt: String,
    val rewards: List<ReplyLetterRewards>
)

data class ReplyLetterRewards(
    val kind: ReplyReward,
    val amount: Int,
    val toastMessage: String? = null
)
