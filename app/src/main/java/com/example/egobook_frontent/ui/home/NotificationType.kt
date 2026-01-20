package com.example.egobook_frontent.ui.home

enum class EgoRoomType {
    WEAKLY_REPORT, DAILY_PRAISE
}

sealed class NotificationType {
    object Letter: NotificationType()
    data class EgoRoom(val type: EgoRoomType): NotificationType()
}
