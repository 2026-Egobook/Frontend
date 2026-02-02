package com.egobook.app.ui.shop

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R

class ItemAdapter :
    ListAdapter<CustomItem, ItemAdapter.ItemViewHolder>(ItemDiffCallback) {
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
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemStatus: TextView = view.findViewById(R.id.tv_item_status)
        val itemPriceIcon: ImageView = view.findViewById(R.id.iv_store_item_ink)
        val itemInfoLayout: LinearLayout = view.findViewById(R.id.ll_item_info)
        val root: View = view.rootView

        fun binding(item: CustomItem) {
            when (item.itemStatus) {
                ItemStatus.PURCHASED -> {
                    itemStatus.text = "보유중"
                    itemInfoLayout.setPadding(24,6,24,6)
                    itemPriceIcon.visibility = GONE
                }
                ItemStatus.SUBSCRIBE_ONLY -> {
                    itemStatus.text = "구독전용"
                    itemInfoLayout.setPadding(24,6,24,6)
                    itemPriceIcon.visibility = GONE
                    root.background = null
                }
                ItemStatus.PURCHASABLE -> {
                    itemStatus.text = item.price.toString()
                    itemInfoLayout.updatePadding(left=12, right=16)
                    itemPriceIcon.visibility = VISIBLE
                    root.background = null
                }
            }
        }
    }

    companion object {
        private val ItemDiffCallback = object : DiffUtil.ItemCallback<CustomItem>() {
            override fun areItemsTheSame(oldItem: CustomItem, newItem: CustomItem): Boolean {
                return oldItem.id == newItem.id // 고유 ID 비교
            }

            override fun areContentsTheSame(oldItem: CustomItem, newItem: CustomItem): Boolean {
                return oldItem == newItem // 전체 객체 내용 비교
            }
        }
    }
}
