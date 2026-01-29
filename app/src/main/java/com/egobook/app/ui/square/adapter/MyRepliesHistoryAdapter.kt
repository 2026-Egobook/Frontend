package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareMyReplyBinding
import com.egobook.app.ui.square.model.question.MyTodayQuestionAnswerItemModel

class MyRepliesHistoryAdapter(
    private val onDeleteClick: (Long) -> Unit
): PagingDataAdapter<MyTodayQuestionAnswerItemModel, MyRepliesHistoryAdapter.MyRepliesHistoryViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRepliesHistoryViewHolder {
        val binding = ItemSquareMyReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRepliesHistoryViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(
        holder: MyRepliesHistoryViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    class MyRepliesHistoryViewHolder(
        private val binding: ItemSquareMyReplyBinding,
        private val onDeleteClick: (Long) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareMyReplyQuestionDatetime.text = item.questionDate
            tvItemSquareMyReplyQuestion.text = "Q. ${item.questionContent}"
            tvItemSquareMyReplyAnswer.text = item.answerContent
            ivItemSquareMyReplyDelete.setOnClickListener {
                onDeleteClick(item.answerId)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MyTodayQuestionAnswerItemModel>() {
            override fun areItemsTheSame(
                oldItem: MyTodayQuestionAnswerItemModel,
                newItem: MyTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem.answerId == newItem.answerId
            }

            override fun areContentsTheSame(
                oldItem: MyTodayQuestionAnswerItemModel,
                newItem: MyTodayQuestionAnswerItemModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}