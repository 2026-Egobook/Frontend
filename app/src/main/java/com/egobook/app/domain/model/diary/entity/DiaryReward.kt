package com.egobook.app.domain.model.diary.entity

data class DiaryRewards(
    val type: List<DiaryType>,
    val rewards: List<DiaryReward>
)

data class DiaryReward(
    val rewardType: RewardType,
    val amount: Int,
    val message: String
)
