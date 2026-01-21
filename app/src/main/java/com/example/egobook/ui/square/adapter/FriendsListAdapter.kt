package com.example.egobook.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook.databinding.ItemSquareFriendListBinding
import com.example.egobook.ui.square.model.FriendModel

class FriendsListAdapter: ListAdapter<FriendModel, FriendsListAdapter.FriendsListViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsListViewHolder {
        val binding = ItemSquareFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendsListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendsListViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class FriendsListViewHolder(private val binding: ItemSquareFriendListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendModel) = with(binding) {
            ivItemFriendListImage.setImageResource(item.image)
            tvItemFriendListLevel.text = "LV ${item.level}"
            tvItemFriendListName.text = item.name
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<FriendModel>() {
            override fun areItemsTheSame(
                oldItem: FriendModel,
                newItem: FriendModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FriendModel,
                newItem: FriendModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}