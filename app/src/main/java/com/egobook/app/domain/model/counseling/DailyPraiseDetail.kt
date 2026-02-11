package com.egobook.app.domain.model.counseling

data class DailyPraiseDetail(
    val diaryDate: String,
    val content: String,
    val createdAt: String,
    val isRead: Boolean,
    val rewards: List<CounselingReward>? = null
)

data class CounselingReward(
    val kind: CounselingRewardType,
    val amount: Int,
    val toastMessage: String
)