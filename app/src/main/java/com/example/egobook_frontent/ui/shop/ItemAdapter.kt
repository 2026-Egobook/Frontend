package com.example.egobook_frontent.ui.shop

import android.R.attr.left
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook_frontent.R

class ItemAdapter(private val items: List<CustomItem>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_store_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
         when (items[position].itemStatus) {
            ItemStatus.PURCHASED -> {
                holder.itemStatus.text = "보유중"
                holder.itemInfoLayout.setPadding(24,6,24,6)
                holder.itemPriceIcon.visibility = GONE
            }
            ItemStatus.SUBSCRIBE_ONLY -> {
                holder.itemStatus.text = "구독전용"
                holder.itemInfoLayout.setPadding(24,6,24,6)
                holder.itemPriceIcon.visibility = GONE
                holder.root.background = null
            }
            ItemStatus.PURCHASABLE -> {
                holder.itemStatus.text = items[position].price.toString()
                holder.itemInfoLayout.updatePadding(left=12, right=16)
                holder.itemPriceIcon.visibility = VISIBLE
                holder.root.background = null
            }
        }
    }

    override fun getItemCount() = items.size

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemStatus: TextView = view.findViewById(R.id.tv_item_status)
        val itemPriceIcon: ImageView = view.findViewById(R.id.iv_store_item_ink)
        val itemInfoLayout: LinearLayout = view.findViewById(R.id.ll_item_info)
        val root: View = view.rootView
    }
}
