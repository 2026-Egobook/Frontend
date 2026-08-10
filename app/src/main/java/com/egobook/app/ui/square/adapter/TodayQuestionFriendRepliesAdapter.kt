package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemSquareFriendAnswerBinding
import com.egobook.app.ui.square.model.question.UserTodayQuestionAnswerItemModel
import com.egobook.app.ui.square.levelBadgeDrawable
import com.egobook.app.ui.square.ProfileImagePlacement
import com.egobook.app.ui.square.tendencyIconDrawable

class TodayQuestionFriendRepliesAdapter: PagingDataAdapter<UserTodayQuestionAnswerItemModel, TodayQuestionFriendRepliesAdapter.TodayQuestionFriendRepliesViewHolder>(diffUtil) {

    class TodayQuestionFriendRepliesViewHolder(private val binding: ItemSquareFriendAnswerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserTodayQuestionAnswerItemModel) = with(binding) {
            tvItemSquareFriendAnswerUserName.text = item.nickname
            tvItemSquareFriendAnswerUserContent.text = item.content
            tvItemSquareFriendAnswerUserLevel.text = "LV ${item.level}"
            ivItemSquareFriendAnswerUserLevel.setImageResource(levelBadgeDrawable(item.level))
            ivItemSquareFriendAnswerSymbol.setImageResource(tendencyIconDrawable(item.topAbilityName))
            ivItemSquareFriendAnswerUserBackground.loadProfileBackground(
                item.backgroundImageUrl,
                ProfileImagePlacement.PLAZA_BACKGROUND
            )
            ivItemSquareFriendAnswerUserImage.loadProfileTurtle(
                item.turtleImageUrl,
                R.drawable.img_temp_square_user_thumbnail,
                ProfileImagePlacement.PLAZA_TURTLE
            )
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
        val diffUtil = object : DiffUtil.ItemCallback<UserTodayQuestionAnswerItemModel>() {
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
