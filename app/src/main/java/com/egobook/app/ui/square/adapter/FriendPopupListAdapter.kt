package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemFriendPopupListBinding
import com.egobook.app.ui.square.model.friend.FriendListModel
import com.egobook.app.ui.square.model.friend.FriendModel

class FriendPopupListAdapter(private val onClicked: (FriendModel) -> Unit): ListAdapter<FriendModel, FriendPopupListAdapter.FriendPopupListViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendPopupListViewHolder {
        val binding = ItemFriendPopupListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendPopupListViewHolder(binding, onClicked)
    }

    override fun onBindViewHolder(
        holder: FriendPopupListViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class FriendPopupListViewHolder(
        private val binding: ItemFriendPopupListBinding,
        private val onClicked: (FriendModel) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: FriendModel) = with(binding) {
            tvItemFriendListName.text = item.name
            root.setOnClickListener { onClicked(item) }
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