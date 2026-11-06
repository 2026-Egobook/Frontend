package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 과거의 나 돌아보기 조회
@Serializable
data class DiaryRecallResponse(
    @SerialName("available")
    val available: Boolean,
    @SerialName("diary")
    val diary: DiaryEntryResponse? = null,
)

// 과거의 나 돌아보기 노출 확정
@Serializable
data class DiaryRecallSeenResponse(
    @SerialName("recalledAt")
    val recalledAt: String? = null,
    @SerialName("nextRecallDate")
    val nextRecallDate: String? = null,
)
