package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareDeferredLetterBinding
import com.egobook.app.ui.square.model.letter.DeferredLetterModel
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SquareDeferredLettersAdapter: PagingDataAdapter<DeferredLetterModel, SquareDeferredLettersAdapter.SquareDeferredLettersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SquareDeferredLettersViewHolder {
        val binding = ItemSquareDeferredLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquareDeferredLettersViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SquareDeferredLettersViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    class SquareDeferredLettersViewHolder(
        private val binding: ItemSquareDeferredLetterBinding,
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeferredLetterModel) = with(binding) {
            tvSquareDeferredLetterFromLabel.text = item.fromLabel
            tvSquareDeferredLetterReplyDeadline.text = getRemainingTime(replyDeadlineAt = item.replyDeadlineAt)
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<DeferredLetterModel>() {
            override fun areItemsTheSame(
                oldItem: DeferredLetterModel,
                newItem: DeferredLetterModel
            ): Boolean {
                return oldItem.letterId == newItem.letterId
            }

            override fun areContentsTheSame(
                oldItem: DeferredLetterModel,
                newItem: DeferredLetterModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}

private fun getRemainingTime(replyDeadlineAt: String): String {
    val deadline: OffsetDateTime = OffsetDateTime.parse(replyDeadlineAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val now: OffsetDateTime = OffsetDateTime.now(ZoneId.systemDefault())
    val duration: Duration = Duration.between(now, deadline)
    return when {
        duration.isNegative || duration.isZero -> "답장 유효기한 만료"
        duration.toDays() > 0 -> "${duration.toDays()}일 남음"
        duration.toHours() > 0 -> "${duration.toHours()}시간 남음"
        duration.toMinutes() > 0 -> "${duration.toMinutes()}분 남음"
        else -> "잠시 후 만료"
    }
}