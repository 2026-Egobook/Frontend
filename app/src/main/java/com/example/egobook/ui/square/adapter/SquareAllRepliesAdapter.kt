package com.example.egobook.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook.databinding.ItemSquareQuestionReplyBinding
import com.example.egobook.ui.square.model.ReplyModel

class SquareAllRepliesAdapter: ListAdapter<ReplyModel, SquareAllRepliesAdapter.SquareAllRepliesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SquareAllRepliesViewHolder {
        val binding = ItemSquareQuestionReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquareAllRepliesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SquareAllRepliesViewHolder,
        position: Int
    ) {
        return holder.bind(getItem(position))
    }

    inner class SquareAllRepliesViewHolder(private val binding: ItemSquareQuestionReplyBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReplyModel) = with(binding) {
            civItemSquareQuestionReplyUserImage.setImageResource(item.image ?: 0)
            tvItemSquareQuestionReplyUserLevel.text = "LV ${item.level}"
            tvItemSquareQuestionReplyUserContent.text = item.answer
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ReplyModel>() {
            override fun areItemsTheSame(
                oldItem: ReplyModel,
                newItem: ReplyModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ReplyModel,
                newItem: ReplyModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}