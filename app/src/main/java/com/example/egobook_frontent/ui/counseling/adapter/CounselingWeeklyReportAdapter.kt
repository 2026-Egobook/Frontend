package com.example.egobook_frontent.ui.counseling.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook_frontent.databinding.ItemCounselingWeeklyReportBinding
import com.example.egobook_frontent.ui.counseling.model.WeeklyReportModel

class CounselingWeeklyReportAdapter(private val onItemClick: (WeeklyReportModel) -> Unit) : ListAdapter<WeeklyReportModel, CounselingWeeklyReportAdapter.WeeklyReportViewHolder>(diffUtil) {

    inner class WeeklyReportViewHolder(private val binding: ItemCounselingWeeklyReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeeklyReportModel) = with(binding) {
            tvCounselingWeeklyReportDatetime.text = item.date
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
        holder.bind(getItem(position))
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