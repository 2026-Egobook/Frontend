package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareQuestionReplyBinding
import com.egobook.app.ui.square.model.question.UserTodayQuestionAnswerItemModel

class SquareAllRepliesAdapter(private val onReportClick: () -> Unit): PagingDataAdapter<UserTodayQuestionAnswerItemModel, SquareAllRepliesAdapter.SquareAllRepliesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SquareAllRepliesViewHolder {
        val binding = ItemSquareQuestionReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquareAllRepliesViewHolder(binding, onReportClick)
    }

    override fun onBindViewHolder(
        holder: SquareAllRepliesViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    class SquareAllRepliesViewHolder(
        private val binding: ItemSquareQuestionReplyBinding,
        private val onReportClick: () -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareQuestionReplyUserContent.text = item.content
            ivSquareQuestionReplyReport.setOnClickListener {
                onReportClick()
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<UserTodayQuestionAnswerItemModel>() {
            override fun areItemsTheSame(
                oldItem: UserTodayQuestionAnswerItemModel,
                newItem: UserTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem.answerId == newItem.answerId
            }

            override fun areContentsTheSame(
                oldItem: UserTodayQuestionAnswerItemModel,
                newItem: UserTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}