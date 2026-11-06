package com.egobook.app.ui.square.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemSquareQuestionReplyBinding
import com.egobook.app.ui.square.model.question.UserTodayQuestionAnswerItemModel
import com.egobook.app.ui.square.levelBadgeDrawable
import com.egobook.app.ui.square.ProfileImagePlacement
import com.egobook.app.ui.square.tendencyIconDrawable

class SquareAllRepliesAdapter(
    private val onReportClick: (Long) -> Unit,
    private val onEmpathyClick: (UserTodayQuestionAnswerItemModel) -> Unit
): PagingDataAdapter<UserTodayQuestionAnswerItemModel, SquareAllRepliesAdapter.SquareAllRepliesViewHolder>(diffUtil) {
    private val expandedAnswerIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SquareAllRepliesViewHolder {
        val binding = ItemSquareQuestionReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquareAllRepliesViewHolder(binding, onReportClick, onEmpathyClick, expandedAnswerIds)
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
        private val onEmpathyClick: (UserTodayQuestionAnswerItemModel) -> Unit,
        private val expandedAnswerIds: MutableSet<Long>
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareQuestionReplyUserContent.text = item.content
            tvItemSquareQuestionReplyUserLevel.text = "LV ${item.level}"
            ivItemSquareQuestionReplyUserLevel.setImageResource(levelBadgeDrawable(item.level))
            ivItemSquareQuestionReplySymbol.setImageResource(tendencyIconDrawable(item.topAbilityName))
            ivItemSquareQuestionReplyUserBackground.loadProfileBackground(
                item.backgroundImageUrl,
                ProfileImagePlacement.PLAZA_BACKGROUND
            )
            ivItemSquareQuestionReplyUserImage.loadProfileTurtle(
                item.turtleImageUrl,
                R.drawable.img_temp_square_user_thumbnail,
                ProfileImagePlacement.PLAZA_TURTLE,
                mirrorFallback = true
            )
            applyContentExpandedState(expandedAnswerIds.contains(item.answerId))
            applyEmpathyState(item.empathized, item.empathyCount)

            root.setOnClickListener {
                toggleContentExpandedState(item.answerId)
            }
            tvItemSquareQuestionReplyUserContent.setOnClickListener {
                toggleContentExpandedState(item.answerId)
            }
            ivSquareQuestionReplyReport.setOnClickListener {
                onReportClick(item.answerId)
            }
            llItemSquareQuestionReplyEmpathy.setOnClickListener {
                onEmpathyClick(item)
            }
        }

        private fun applyEmpathyState(isEmpathized: Boolean, count: Long) = with(binding) {
            ivItemSquareQuestionReplyEmpathy.setImageResource(
                if (isEmpathized) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
            tvItemSquareQuestionReplyEmpathyCount.text = formatEmpathyCount(count)
            tvItemSquareQuestionReplyEmpathyCount.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (isEmpathized) R.color.brand else R.color.neutral_subtle
                )
            )
        }

        private fun formatEmpathyCount(count: Long): String =
            if (count > MAX_DISPLAY_EMPATHY_COUNT) "$MAX_DISPLAY_EMPATHY_COUNT+" else count.toString()

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
        private const val MAX_DISPLAY_EMPATHY_COUNT = 99

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
