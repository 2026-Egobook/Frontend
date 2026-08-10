package com.egobook.app.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.egobook.app.R

/**
 * 앱에서 사용하는 알림 채널 정의.
 *
 * 채널은 생성 이후 중요도(importance)를 코드로 변경할 수 없으므로,
 * 새로운 성격의 알림이 필요하면 기존 채널을 수정하지 말고 채널을 추가한다.
 */
object NotificationChannels {
    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService<NotificationManager>() ?: return
        val channel =
            NotificationChannel(
                context.getString(R.string.default_notification_channel_id),
                context.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.default_notification_channel_description)
            }
        manager.createNotificationChannel(channel)
    }
}
