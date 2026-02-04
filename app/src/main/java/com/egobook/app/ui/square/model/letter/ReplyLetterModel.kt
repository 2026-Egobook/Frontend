package com.egobook.app.ui.square.model.letter

import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.ReplyLetter
import com.egobook.app.domain.model.square.letter.ReplyLetterRewards
import com.egobook.app.domain.model.square.letter.ReplyReward

data class ReplyLetterModel(
    val letterId: Long,
    val status: LetterStatus,
    val repliedAt: String,
    val rewards: List<ReplyLetterRewardsModel>
)

data class ReplyLetterRewardsModel(
    val kind: ReplyReward,
    val amount: Int,
    val toastMessage: String? = null
)

fun ReplyLetterRewards.toPresentation(): ReplyLetterRewardsModel = ReplyLetterRewardsModel(
    kind = kind,
    amount = amount,
    toastMessage = toastMessage
)

fun ReplyLetter.toPresentation(): ReplyLetterModel = ReplyLetterModel(
    letterId = letterId,
    status = status,
    repliedAt = repliedAt,
    rewards = rewards.map { it.toPresentation() }
)