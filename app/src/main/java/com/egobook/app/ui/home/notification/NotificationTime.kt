package com.egobook.app.ui.home.notification

import java.time.Duration
import java.time.LocalDateTime

@JvmInline
value class NotificationTime(val time: LocalDateTime) {
    fun betweenMinutes(currentTime: NotificationTime): Long {
        val duration = Duration.between(time, currentTime.time).toMinutes()
        require(duration >= 0) { "현재 시간이 과거(알림 생성 시점)보다 이전일 수 없습니다."}
        return duration
    }
}
