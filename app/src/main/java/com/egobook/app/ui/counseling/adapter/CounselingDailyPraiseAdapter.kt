package com.egobook.app.ui.counseling.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemCounselingDailyPraiseBinding
import com.egobook.app.ui.counseling.model.PraiseDailyModel
import com.egobook.app.ui.counseling.model.PraiseMessageModel

class CounselingDailyPraiseAdapter: PagingDataAdapter<PraiseDailyModel, CounselingDailyPraiseAdapter.PraiseViewHolder>(diffUtil) {

    inner class PraiseViewHolder(private val binding: ItemCounselingDailyPraiseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PraiseDailyModel) = with(binding) {
            tvCounselingDailyPraiseDatetime.text = item.diaryDate
            root.setOnClickListener {
                cvCounselingDailyPraiseContent.isVisible = !cvCounselingDailyPraiseContent.isVisible
                ivCounselingDailyPraiseToggle.setImageResource(if(cvCounselingDailyPraiseContent.isVisible) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PraiseViewHolder {
        val binding = ItemCounselingDailyPraiseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PraiseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PraiseViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PraiseDailyModel>() {
            override fun areItemsTheSame(oldItem: PraiseDailyModel, newItem: PraiseDailyModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PraiseDailyModel, newItem: PraiseDailyModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}