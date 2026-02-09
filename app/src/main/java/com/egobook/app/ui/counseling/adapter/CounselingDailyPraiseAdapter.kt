package com.egobook.app.ui.counseling.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemCounselingDailyPraiseBinding
import com.egobook.app.ui.counseling.model.DailyPraiseDetailModel
import com.egobook.app.ui.counseling.model.PraiseDailyModel

class CounselingDailyPraiseAdapter(private val onItemClicked: (String) -> Unit): PagingDataAdapter<PraiseDailyModel, CounselingDailyPraiseAdapter.PraiseViewHolder>(diffUtil) {

    // 날짜별 상세 데이터를 저장할 별도 공간
    private val detailsMap = mutableMapOf<String, DailyPraiseDetailModel>()

    inner class PraiseViewHolder(private val binding: ItemCounselingDailyPraiseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PraiseDailyModel) = with(binding) {
            tvCounselingDailyPraiseDatetime.text = item.diaryDate

            val detail = detailsMap[item.diaryDate]
            if(detail != null) {
                // 클릭한 경우
                cvCounselingDailyPraiseContent.isVisible = true
                tvCounselingDailyPraiseContent.text = detail.content
                ivCounselingDailyPraiseToggle.setImageResource(R.drawable.ic_chevron_up)
                if(!detail.isRead) {
                    Toast.makeText(root.context, detail.rewards?.first()?.toastMessage, Toast.LENGTH_SHORT).show()
                    // TODO: 실제 유저의 자존감 능력치 올리기
                }
            } else {
                // 아직 클릭하지 않은 경우
                cvCounselingDailyPraiseContent.isVisible = false
                ivCounselingDailyPraiseToggle.setImageResource(R.drawable.ic_chevron_down)
            }

            root.setOnClickListener {
                if(detailsMap.containsKey(item.diaryDate)) {
                    // 열린 상태 → 닫아야 함
                    detailsMap.remove(item.diaryDate)
                    notifyItemChanged(bindingAdapterPosition)
                } else {
                    onItemClicked(item.diaryDate)
                }
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

    fun updateItem(item: DailyPraiseDetailModel) {
        detailsMap[item.diaryDate] = item
        val currentList = snapshot().items
        val index = currentList.indexOfFirst { it.diaryDate == item.diaryDate }
        if(index != -1) {
            notifyItemChanged(index)
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