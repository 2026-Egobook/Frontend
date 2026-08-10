package com.egobook.app.ui.counseling.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemCounselingWeeklyReportBinding
import com.egobook.app.ui.counseling.model.WeeklyReportModel

class CounselingWeeklyReportAdapter(private val onItemClick: (WeeklyReportModel) -> Unit) : PagingDataAdapter<WeeklyReportModel, CounselingWeeklyReportAdapter.WeeklyReportViewHolder>(diffUtil) {

    inner class WeeklyReportViewHolder(private val binding: ItemCounselingWeeklyReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeeklyReportModel) = with(binding) {
            tvCounselingWeeklyReportDatetime.text = "${item.startDate} ~ ${item.endDate}"
            val showMoreIcon = if(item.isLocked) R.drawable.ic_lock_key else R.drawable.ic_chevron_right
            ivCounselingWeeklyReportViewMore.setImageResource(showMoreIcon)
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyReportViewHolder {
        val binding = ItemCounselingWeeklyReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeeklyReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyReportViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) (holder.bind(item))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WeeklyReportModel>() {
            override fun areItemsTheSame(
                oldItem: WeeklyReportModel,
                newItem: WeeklyReportModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WeeklyReportModel,
                newItem: WeeklyReportModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}