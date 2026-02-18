package com.egobook.app.ui.diary.model

data class ToastMessage(
    val rewardType: String,
    val message: String,
    val amount: Int,
    val imageRes: Int,  // 토스트에 표시될 이미지 리소스 ID
)
