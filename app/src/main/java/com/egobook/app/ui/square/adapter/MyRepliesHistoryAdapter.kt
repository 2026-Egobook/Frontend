package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareMyReplyBinding
import com.egobook.app.ui.square.model.friend.ReplyModel

class MyRepliesHistoryAdapter: ListAdapter<ReplyModel, MyRepliesHistoryAdapter.MyRepliesHistoryViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRepliesHistoryViewHolder {
        val binding = ItemSquareMyReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRepliesHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyRepliesHistoryViewHolder,
        position: Int
    ) {
        return holder.bind(getItem(position))
    }

    class MyRepliesHistoryViewHolder(private val binding: ItemSquareMyReplyBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReplyModel) = with(binding) {
            tvItemSquareMyReplyDatetime.text = item.date
            tvItemSquareMyReplyQuestion.text = item.question
            tvItemSquareMyReplyAnswer.text = item.answer
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