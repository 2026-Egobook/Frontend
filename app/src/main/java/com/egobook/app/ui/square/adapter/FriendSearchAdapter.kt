package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.egobook.app.R
import com.egobook.app.databinding.ItemSearchFriendBinding
import com.egobook.app.ui.square.model.friend.SearchUserModel

/**
 * 1. fallback(R.drawable.default_turtle) → null일 때 보여주는 기본 이미지 지정
 */
class FriendSearchAdapter(
    private val onApply: (Long, Int) -> Unit
) : ListAdapter<SearchUserModel, FriendSearchAdapter.FriendSearchViewHolder>(diffUtil) {

    private var isApplied = false

    inner class FriendSearchViewHolder(val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchUserModel) = with(binding) {
            btnAddFriendSearchResultApply.isEnabled = true
            btnAddFriendSearchResultApply.alpha = 1f
            btnAddFriendSearchResultApply.text = "신청하기"
            tvAddFriendSearchLevel.text = "LV ${item.level}"
            tvAddFriendSearchNickname.text = item.nickname
            Glide.with(ivAddFriendSearchImage).load(item.profileImageUrl)
                .fallback(R.drawable.default_turtle).into(ivAddFriendSearchImage) // 1
            btnAddFriendSearchResultApply.setOnClickListener { onApply(item.userId, bindingAdapterPosition) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendSearchViewHolder {
        val binding = ItemSearchFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SearchUserModel>() {
            override fun areItemsTheSame(
                oldItem: SearchUserModel,
                newItem: SearchUserModel
            ): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(
                oldItem: SearchUserModel,
                newItem: SearchUserModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}