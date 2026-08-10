package com.egobook.app.push

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.domain.repository.push.PushTokenRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class EgobookMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var pushTokenRepository: PushTokenRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * 토큰이 새로 발급되거나 갱신될 때 호출된다.
     * 로그인 전이라면 등록에 실패하는데, 이 경우 홈 진입 시 재시도된다.
     */
    override fun onNewToken(token: String) {
        Timber.d("FCM 토큰이 갱신되었습니다.")
        serviceScope.launch {
            pushTokenRepository.registerCurrentToken()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data[KEY_TITLE]
        val body = message.notification?.body ?: message.data[KEY_BODY]

        if (title == null && body == null) {
            Timber.w("표시할 내용이 없는 메시지를 수신했습니다: %s", message.data)
            return
        }

        showNotification(title, body)
    }

    private fun showNotification(
        title: String?,
        body: String?,
    ) {
        if (!hasPostNotificationPermission()) {
            Timber.w("POST_NOTIFICATIONS 권한이 없어 알림을 표시하지 않습니다.")
            return
        }

        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.brand))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(notificationId.incrementAndGet(), notification)
    }

    private fun hasPostNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"

        private val notificationId = AtomicInteger(0)
    }
}
