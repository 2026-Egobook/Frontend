package com.egobook.app.ui.square.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemSquareQuestionReplyBinding
import com.egobook.app.ui.square.model.question.UserTodayQuestionAnswerItemModel

class SquareAllRepliesAdapter(private val onReportClick: (Long) -> Unit): PagingDataAdapter<UserTodayQuestionAnswerItemModel, SquareAllRepliesAdapter.SquareAllRepliesViewHolder>(diffUtil) {
    private val expandedAnswerIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SquareAllRepliesViewHolder {
        val binding = ItemSquareQuestionReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquareAllRepliesViewHolder(binding, onReportClick, expandedAnswerIds)
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
        private val onReportClick: (Long) -> Unit,
        private val expandedAnswerIds: MutableSet<Long>
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareQuestionReplyUserContent.text = item.content
            ivItemSquareQuestionReplyUserBackground.loadProfileBackground(item.backgroundImageUrl)
            ivItemSquareQuestionReplyUserImage.loadProfileTurtle(
                item.turtleImageUrl,
                R.drawable.img_temp_square_user_thumbnail
            )
            applyContentExpandedState(expandedAnswerIds.contains(item.answerId))

            root.setOnClickListener {
                toggleContentExpandedState(item.answerId)
            }
            tvItemSquareQuestionReplyUserContent.setOnClickListener {
                toggleContentExpandedState(item.answerId)
            }
            ivSquareQuestionReplyReport.setOnClickListener {
                onReportClick(item.answerId)
            }
        }

        private fun applyContentExpandedState(isExpanded: Boolean) = with(binding.tvItemSquareQuestionReplyUserContent) {
            maxLines = if (isExpanded) Int.MAX_VALUE else COLLAPSED_CONTENT_MAX_LINES
            ellipsize = if (isExpanded) null else TextUtils.TruncateAt.END
        }

        private fun toggleContentExpandedState(answerId: Long) {
            val isExpanded = if (expandedAnswerIds.contains(answerId)) {
                expandedAnswerIds.remove(answerId)
                false
            } else {
                expandedAnswerIds.add(answerId)
                true
            }
            applyContentExpandedState(isExpanded)
        }
    }

    companion object {
        private const val COLLAPSED_CONTENT_MAX_LINES = 4

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
