package com.egobook.app.store.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.egobook.app.R
import com.egobook.app.store.data.model.ItemStatus

class ItemAdapter(
    private val onItemClick: (CustomItem) -> Unit
) : ListAdapter<CustomItemState, ItemAdapter.ItemViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_store_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding(getItem(position))
        holder.root.setOnClickListener {
            onItemClick(getItem(position).item)
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemStatus: TextView = view.findViewById(R.id.tv_item_status)
        val itemPriceIcon: ImageView = view.findViewById(R.id.iv_store_item_ink)
        val itemInfoLayout: LinearLayout = view.findViewById(R.id.ll_item_info)
        val itemImage: ImageView = view.findViewById(R.id.iv_item)
        val root: View = view.rootView

        fun binding(itemState: CustomItemState) {
            val item = itemState.item
            item.image?.let { image ->
                when(image) {
                    is ItemImage.Url -> itemImage.load(image.path)
                }
            }

            when (item.itemStatus) {
                ItemStatus.PURCHASED -> {
                    itemStatus.text = "보유중"
                    itemInfoLayout.setPadding(24,6,24,6)
                    itemPriceIcon.visibility = View.GONE
                }
                ItemStatus.SUBSCRIBE_ONLY -> {
                    itemStatus.text = "구독전용"
                    itemInfoLayout.setPadding(24,6,24,6)
                    itemPriceIcon.visibility = View.GONE
                }
                ItemStatus.PURCHASABLE -> {
                    itemStatus.text = item.price.toString()
                    itemInfoLayout.updatePadding(left=12, right=16)
                    itemPriceIcon.visibility = View.VISIBLE
                }
            }

            if (itemState.isSelected) {
                root.setBackgroundResource(R.drawable.store_item_selection_border)
            } else {
                root.background = null
            }
        }
    }

    companion object {
        private val ItemDiffCallback = object : DiffUtil.ItemCallback<CustomItemState>() {
            override fun areItemsTheSame(oldItem: CustomItemState, newItem: CustomItemState): Boolean {
                return oldItem.item.id == newItem.item.id // 고유 ID 비교
            }

            override fun areContentsTheSame(oldItem: CustomItemState, newItem: CustomItemState): Boolean {
                return oldItem == newItem // 전체 객체 내용 비교
            }
        }
    }
}
