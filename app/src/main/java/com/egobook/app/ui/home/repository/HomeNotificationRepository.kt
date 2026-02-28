package com.egobook.app.ui.home.repository

import android.util.Log
import com.egobook.app.di.qualifier.BackendApi
import com.egobook.app.ui.home.notification.EgoRoomType
import com.egobook.app.ui.home.notification.Notification
import com.egobook.app.ui.home.notification.NotificationPublisher
import com.egobook.app.ui.home.notification.NotificationStatus
import com.egobook.app.ui.home.notification.NotificationTime
import com.egobook.app.ui.home.notification.NotificationType
import com.egobook.app.store.data.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface HomeNotificationRepository {
    suspend fun loadNotifications(): Flow<Notification>
    suspend fun loadNotificationSetting(): NotificationSettingDto
    suspend fun changeNotificationSetting(): NotificationSettingDto

    suspend fun readNotification(notification: Notification): NotificationReadingDto
}

data class NotificationReadingDto(
    val notificationId: Int,
    val isRead: Boolean
)

data class NotificationSettingDto(
    val enabled: Boolean
)

data class NotificationGroupDto(
    val content: List<NotificationDto>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

fun String.toNotificationType(): NotificationType = when (this) {
    "LETTER_REPLY", "LETTER_REPLY_FRIEND", "LETTER_NEW", "LETTER_NEW_FRIEND" -> NotificationType.Letter
    "PRAISE" -> NotificationType.EgoRoom(EgoRoomType.DAILY_PRAISE)
    "REPORT" -> NotificationType.EgoRoom(EgoRoomType.WEAKLY_REPORT)
    else -> throw IllegalArgumentException("${this}은 알 수 없는 알림 타입 이름입니다")
}

data class NotificationDto(
    val notificationId: Int,
    val type: String,
    val title: String,
    val content: String?,
    val isRead: Boolean,
    val targetId: Int,
    val createdAt: String
) {
    fun toDomain(): Notification {
        return Notification(
            id = notificationId,
            content = content ?: "내용이 없습니다",
            type = type.toNotificationType(),
            status = if (isRead) NotificationStatus.READ else NotificationStatus.UNREAD,
            publisher = NotificationPublisher.User("admin", "운영자"),
            publishedDate = NotificationTime(LocalDateTime.parse(createdAt)),
        )
    }

}

interface NetworkNotificationLoadingService {
    @GET("/notifications")
    suspend fun loadNotificationsResponse(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<NotificationGroupDto>

    @GET("/notifications/settings")
    suspend fun loadNotificationSetting(): BaseResponse<NotificationSettingDto>

    @PATCH("/notifications/settings")
    suspend fun changeNotificationSetting(): BaseResponse<NotificationSettingDto>

    @POST("/notifications/{notificationId}/read")
    suspend fun readNotification(
        @Path("notificationId") notificationId: Int,
    ): BaseResponse<NotificationReadingDto>
}


@Singleton
class NetworkHomeNotificationRepository @Inject constructor(
    @BackendApi private val retrofit: Retrofit
) : HomeNotificationRepository {

    private val loadingNotificationService by lazy {
        retrofit.create(NetworkNotificationLoadingService::class.java)
    }

    override suspend fun loadNotifications(): Flow<Notification> {
        return flow {
            var currentPage = 1
            while (true) {
                try {
                    val response = loadingNotificationService.loadNotificationsResponse(
                        page = currentPage,
                        size = 10
                    ).data

                    response.content.forEach { dto ->
                        Log.d("jang", "$dto")
                        Log.d("jang", "${NotificationTime(LocalDateTime.parse(dto.createdAt))}")
                        emit(dto.toDomain())
                    }

                    if (!response.hasNext) {
                        break
                    }
                    currentPage++

                } catch (e: Exception) {
                    Log.e(
                        "jang",
                        "페이지 $currentPage 로드 중 에러 발생: $e"
                    )
                    break
                }
            }
        }
    }

    override suspend fun loadNotificationSetting(): NotificationSettingDto {
        return loadingNotificationService.loadNotificationSetting().data
    }

    override suspend fun changeNotificationSetting(): NotificationSettingDto {
        return loadingNotificationService.changeNotificationSetting().data
    }

    override suspend fun readNotification(notification: Notification): NotificationReadingDto {
        return loadingNotificationService.readNotification(notification.id).data
    }
}
