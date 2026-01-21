package com.example.egobook.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook.databinding.ItemSquareMyReplyBinding
import com.example.egobook.ui.square.model.MyReplyModel

class MyRepliesHistoryAdapter: ListAdapter<MyReplyModel, MyRepliesHistoryAdapter.MyRepliesHistoryViewHolder>(diffUtil) {
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
        fun bind(item: MyReplyModel) = with(binding) {
            tvItemSquareMyReplyDatetime.text = item.date
            tvItemSquareMyReplyQuestion.text = item.question
            tvItemSquareMyReplyAnswer.text = item.answer
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MyReplyModel>() {
            override fun areItemsTheSame(
                oldItem: MyReplyModel,
                newItem: MyReplyModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MyReplyModel,
                newItem: MyReplyModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}