package com.egobook.app.data.model.square.letter

import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.ReplyLetterRewards
import com.egobook.app.domain.model.square.letter.ReplyReward
import com.google.gson.annotations.SerializedName

data class ReplyLetterResponse(
    @SerializedName("letterId")
    val letterId: Long,
    @SerializedName("status")
    val status: LetterStatus,
    @SerializedName("repliedAt")
    val repliedAt: String,
    @SerializedName("rewards")
    val rewards: List<ReplyLetterRewardsResponse>
)

data class ReplyLetterRewardsResponse(
    @SerializedName("kind")
    val kind: ReplyReward,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("toastMessage")
    val toastMessage: String? = null
)

fun ReplyLetterResponse.toDomain(): ReplyLetter = ReplyLetter(
    letterId = letterId,
    status = status,
    repliedAt = repliedAt,
    rewards = rewards.map { it.toDomain() }
)

fun ReplyLetterRewardsResponse.toDomain(): ReplyLetterRewards = ReplyLetterRewards(
    kind = kind,
    amount = amount,
    toastMessage = toastMessage
)


