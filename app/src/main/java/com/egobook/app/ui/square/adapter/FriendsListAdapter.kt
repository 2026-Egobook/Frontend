package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareFriendListBinding
import com.egobook.app.ui.square.model.FriendModel

class FriendsListAdapter(private val onDeleted: (FriendModel) -> Unit): ListAdapter<FriendModel, FriendsListAdapter.FriendsListViewHolder>(diffUtil) {

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

    inner class FriendsListViewHolder(private val binding: ItemSquareFriendListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendModel) = with(binding) {
            ivItemFriendListImage.setImageResource(item.image)
            tvItemFriendListLevel.text = "LV ${item.level}"
            tvItemFriendListName.text = item.name
            ivItemSquareFriendListDelete.setOnClickListener { onDeleted(item) }
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