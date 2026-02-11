package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//일기 생성
@Serializable
data class DiaryCreateResponse(
    @SerialName("entry")
    val entry: DiaryEntryResponse,
    @SerialName("rewards")
    val rewards: List<Reward>
)
@Serializable
data class Reward(
    @SerialName("rewardType")
    val rewardType: String,
    @SerialName("amount")
    val amount: Int,
    @SerialName("message")
    val message: String
)