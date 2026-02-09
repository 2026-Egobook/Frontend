package com.egobook.app.data.model.counseling

import com.google.gson.annotations.SerializedName

data class CounselingNotificationRequest(
    @SerializedName("enabled")
    val enabled: Boolean
)
