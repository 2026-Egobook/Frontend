package com.egobook.app.data.model.diary.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryDeleteResponse (
    @SerialName("deleted")
    val deleted: Boolean
)