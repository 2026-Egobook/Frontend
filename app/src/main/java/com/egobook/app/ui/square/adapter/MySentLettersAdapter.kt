package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareLetterBinding
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.ui.square.model.letter.SentLetterModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MySentLettersAdapter(private val onClicked: (Long) -> Unit): PagingDataAdapter<SentLetterModel, MySentLettersAdapter.MyLetterViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyLetterViewHolder {
        return MyLetterViewHolder(ItemSquareLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: MyLetterViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item != null) holder.bind(item)
    }

    inner class MyLetterViewHolder(private val binding: ItemSquareLetterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SentLetterModel) = with(binding) {
            tvItemSquareLetterDatetime.text = formatDate(createdDateTime = item.createdAt)
            ivItemLetter.isVisible = item.status == LetterStatus.REPLIED || item.status == LetterStatus.AI_REPLIED
            root.setOnClickListener {
                onClicked(item.letterId)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<SentLetterModel>() {
            override fun areItemsTheSame(
                oldItem: SentLetterModel,
                newItem: SentLetterModel
            ): Boolean {
                return oldItem.letterId == newItem.letterId
            }

            override fun areContentsTheSame(
                oldItem: SentLetterModel,
                newItem: SentLetterModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private fun formatDate(createdDateTime: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return createdDateTime.toLocalDateOrNull()?.format(formatter).orEmpty()
    }

    private fun String.toLocalDateOrNull(): LocalDate? {
        return runCatching {
            Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDate()
        }.recoverCatching {
            OffsetDateTime.parse(this).toLocalDate()
        }.recoverCatching {
            LocalDateTime.parse(this).toLocalDate()
        }.getOrNull()
    }
}
