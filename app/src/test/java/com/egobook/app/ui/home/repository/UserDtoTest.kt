package com.egobook.app.ui.home.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserDtoTest {

    @Test
    fun `읽지 않은 공지가 있으면 사용자 상태에 그대로 전달한다`() {
        assertThat(dto(hasUnreadNotice = true).toDomain().hasUnreadNotice).isTrue()
    }

    @Test
    fun `읽지 않은 공지가 없으면 사용자 상태에 그대로 전달한다`() {
        assertThat(dto(hasUnreadNotice = false).toDomain().hasUnreadNotice).isFalse()
    }

    private fun dto(hasUnreadNotice: Boolean) = UserDto(
        userId = 10,
        nickname = "에고북1234",
        level = 1,
        ink = 100,
        unreadNotifications = 10,
        hasUnreadNotice = hasUnreadNotice,
        hasUnopenedPsychology = true,
        isFirstAttendanceToday = true,
        attendanceRewardInk = 3
    )
}
