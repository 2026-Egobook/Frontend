package com.example.egobook_frontent.ui.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.egobook_frontent.databinding.ItemDiaryBinding

class DiaryRVAdapter(private val diaryList: List<Diary>) :
    RecyclerView.Adapter<DiaryRVAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): DiaryRVAdapter.ViewHolder {
        val binding: ItemDiaryBinding = ItemDiaryBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: DiaryRVAdapter.ViewHolder, position: Int) {
        holder.bind(diaryList[position])
    }

    override fun getItemCount(): Int = diaryList.size

    inner class ViewHolder(var binding: ItemDiaryBinding):
            RecyclerView.ViewHolder(binding.root) {
                fun bind(diary: Diary) {
                    binding.tvDiaryContent.text = diary.content
                    binding.tvTime.text = diary.time
                }
            }
}