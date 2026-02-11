package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.counseling.CounselingReward
import com.egobook.app.domain.model.counseling.CounselingRewardType
import com.egobook.app.domain.model.counseling.DailyPraiseDetail
import com.google.gson.annotations.SerializedName

data class DailyPraiseResponse(
    @SerializedName("diaryDate")
    val diaryDate: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("rewards")
    val rewards: List<CounselingRewardResponse>? = null
)

data class CounselingRewardResponse(
    @SerializedName("kind")
    val kind: CounselingRewardType,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("toastMessage")
    val toastMessage: String
)

fun CounselingRewardResponse.toDomain() = CounselingReward(
    kind = kind,
    amount = amount,
    toastMessage = toastMessage
)

fun DailyPraiseResponse.toDomain() = DailyPraiseDetail(
    diaryDate = diaryDate,
    content = content,
    createdAt = createdAt,
    isRead = isRead,
    rewards = rewards?.map { it.toDomain() }
)