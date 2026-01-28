package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareFriendAnswerBinding
import com.egobook.app.ui.square.model.question.FriendTodayQuestionAnswerItemModel

class TodayQuestionFriendRepliesAdapter: PagingDataAdapter<FriendTodayQuestionAnswerItemModel, TodayQuestionFriendRepliesAdapter.TodayQuestionFriendRepliesViewHolder>(diffUtil) {

    class TodayQuestionFriendRepliesViewHolder(private val binding: ItemSquareFriendAnswerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareFriendAnswerUserName.text = item.nickname
            tvItemSquareFriendAnswerUserContent.text = item.content
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodayQuestionFriendRepliesViewHolder {
        val binding = ItemSquareFriendAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodayQuestionFriendRepliesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TodayQuestionFriendRepliesViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FriendTodayQuestionAnswerItemModel>() {
            override fun areItemsTheSame(
                oldItem: FriendTodayQuestionAnswerItemModel,
                newItem: FriendTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem.answerId == newItem.answerId
            }

            override fun areContentsTheSame(
                oldItem: FriendTodayQuestionAnswerItemModel,
                newItem: FriendTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}