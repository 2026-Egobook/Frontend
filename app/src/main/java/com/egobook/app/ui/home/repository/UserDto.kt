package com.egobook.app.ui.home.repository

import com.egobook.app.ui.home.user.Ink
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.User

data class UserDto(
    val userId: Int,
    val nickname: String,
    val level: Int,
    val ink: Int,
    val unreadNotifications: Int,
    val hasUnreadNotice: Boolean = false,
    val hasUnopenedPsychology: Boolean,
    val isFirstAttendanceToday: Boolean,
    val attendanceRewardInk: Int
) {
     fun toDomain(): User = User(
         id = userId,
         Level(level),
         Ink(ink),
         nickname = nickname,
         hasUnreadNotice = hasUnreadNotice
     )
}
