package com.example.egobook_frontent.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook_frontent.R

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
    }

    override fun getItemCount() = notifications.size

    class NotificationViewHodler(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_notification_title)
        val content: TextView = view.findViewById(R.id.tv_notification_content)
        val time: TextView = view.findViewById(R.id.tv_notification_time)
    }

}
