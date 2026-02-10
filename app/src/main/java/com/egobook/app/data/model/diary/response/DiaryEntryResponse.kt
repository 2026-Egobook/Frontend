package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//일기 상세 확인, 일기 수정 이 직접 사용, 일기 생성에서 부분 사용
@Serializable
data class DiaryEntryResponse (
    @SerialName("diaryId")
    val diaryId: Long,

    @SerialName("date")
    val date: String,

    @SerialName("writtenAt")
    val writtenAt: String,

    @SerialName("type")
    val type: List<String>,

    @SerialName("emotionLevel")
    val emotionLevel: Int?,

    @SerialName("content")
    val content: String,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String

)
