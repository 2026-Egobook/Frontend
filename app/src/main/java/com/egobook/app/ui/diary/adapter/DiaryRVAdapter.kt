package com.egobook.app.ui.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemDiaryBinding
import com.egobook.app.domain.model.Diary

class DiaryRVAdapter(private val diaryList: List<Diary>) : RecyclerView.Adapter<DiaryRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(diary: Diary)
    }

    private lateinit var myItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemDiaryBinding = ItemDiaryBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(diaryList[position])

        holder.itemView.setOnClickListener {
            myItemClickListener.onItemClick(diaryList[position])
        }
    }

    override fun getItemCount(): Int = diaryList.size

    inner class ViewHolder(var binding: ItemDiaryBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(diary: Diary) {
                    binding.tvDiaryContent.text = diary.content
                    binding.tvTime.text = diary.time
                }
            }
}