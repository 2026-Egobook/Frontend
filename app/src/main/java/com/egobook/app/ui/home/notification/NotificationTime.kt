package com.egobook.app.ui.home.notification

import java.time.Duration
import java.time.LocalDateTime

@JvmInline
value class NotificationTime(val time: LocalDateTime) {
    fun betweenMinutes(currentTime: NotificationTime): Long {
        val duration = Duration.between(time, currentTime.time).toMinutes()
        return duration.coerceAtLeast(0)
    }
}
