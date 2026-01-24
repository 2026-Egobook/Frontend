package com.egobook.app.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import java.time.LocalDateTime

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHodler>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHodler {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_home_notification, parent, false)
        return NotificationViewHodler(view)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHodler,
        position: Int
    ) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    class NotificationViewHodler(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_notification_title)
        val content: TextView = view.findViewById(R.id.tv_notification_content)
        val time: TextView = view.findViewById(R.id.tv_notification_time)
        val icon: ImageView = view.findViewById(R.id.iv_notification_icon)

        fun bind(notification: Notification) {
            val publisherName = when (notification.publisher) {
                is NotificationPublisher.Admin -> ""
                is NotificationPublisher.User -> notification.publisher.userName
            }

            val publishMonth = notification.publishedDate.time.month.value
            val publishDate = notification.publishedDate.time.dayOfMonth

            when (notification.type) {
                is NotificationType.Letter -> {
                    icon.setImageResource(R.drawable.ic_unread_letter_notification)
                    title.text =
                        if (publisherName.isNotEmpty()) "$publisherName 의 편지 답장이 도착했어요" else "편지 답장이 도착했어요"
                    content.text = notification.content
                }

                is NotificationType.EgoRoom -> {
                    icon.setImageResource(R.drawable.ic_unread_ego_notification)
                    content.visibility = GONE
                    when (notification.type.type) {
                        EgoRoomType.DAILY_PRAISE -> {
                            title.text = "$publishMonth.$publishDate 일간 칭찬서가 도착했어요!"
                        }

                        EgoRoomType.WEAKLY_REPORT -> {
                            title.text = "지난주 주간 리포트가 도착했어요!"
                        }
                    }
                }
            }

            val timeDifferenceMinutes =
                notification.publishedDate.betweenMinutes(NotificationTime(LocalDateTime.now()))
            val timeDifferenceHours = timeDifferenceMinutes / 60
            time.text = when (timeDifferenceHours) {
                0L -> "$timeDifferenceMinutes 분 전"
                in 1L..24L -> "$timeDifferenceHours 시간 전"
                else -> "$publishMonth.$publishDate"
            }

            when (notification.status) {
                NotificationStatus.READ -> {
                    val neutralSubtleColor = Color.parseColor("#969696")
                    title.setTextColor(neutralSubtleColor)
                    content.setTextColor(neutralSubtleColor)
                    time.setTextColor(neutralSubtleColor)
                }

                NotificationStatus.UNREAD -> {
                    val neutralColor = Color.parseColor("#191818")
                    title.setTextColor(neutralColor)
                    content.setTextColor(neutralColor)
                    time.setTextColor(neutralColor)
                }
            }


        }
    }

}
