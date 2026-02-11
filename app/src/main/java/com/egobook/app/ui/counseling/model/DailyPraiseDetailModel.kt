package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.counseling.CounselingReward
import com.egobook.app.domain.model.counseling.CounselingRewardType
import com.egobook.app.domain.model.counseling.DailyPraiseDetail

data class DailyPraiseDetailModel(
    val diaryDate: String,
    val content: String,
    val createdAt: String,
    val isRead: Boolean,
    val rewards: List<CounselingRewardModel>? = null
)

data class CounselingRewardModel(
    val kind: CounselingRewardType,
    val amount: Int,
    val toastMessage: String
)

fun CounselingReward.toPresentation() = CounselingRewardModel(
    kind = kind,
    amount = amount,
    toastMessage = toastMessage
)

fun DailyPraiseDetail.toPresentation() = DailyPraiseDetailModel(
    diaryDate = diaryDate,
    content = content,
    createdAt = createdAt,
    isRead = isRead,
    rewards = rewards?.map { it.toPresentation() }
)