package com.example.egobook_frontent.ui.counseling.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.ItemCounselingDailyPraiseBinding
import com.example.egobook_frontent.ui.counseling.model.PraiseMessageModel

class CounselingDailyPraiseAdapter: ListAdapter<PraiseMessageModel, CounselingDailyPraiseAdapter.PraiseViewHolder>(diffUtil) {

    inner class PraiseViewHolder(private val binding: ItemCounselingDailyPraiseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PraiseMessageModel) = with(binding) {
            tvCounselingDailyPraiseDatetime.text = item.formattedDate
            tvCounselingDailyPraiseContent.text = item.messageText
            ivCounselingDailyPraiseToggle.setOnClickListener {
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
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PraiseMessageModel>() {
            override fun areItemsTheSame(oldItem: PraiseMessageModel, newItem: PraiseMessageModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PraiseMessageModel, newItem: PraiseMessageModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}