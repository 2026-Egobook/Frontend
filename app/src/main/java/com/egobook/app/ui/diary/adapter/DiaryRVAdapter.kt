package com.egobook.app.ui.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemDiaryBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.ui.diary.util.toTimeString

class DiaryRVAdapter :
    ListAdapter<Diary, DiaryRVAdapter.ViewHolder>(DiaryDiffCallback()) {

    interface MyItemClickListener {
        fun onItemClick(diary: Diary)
    }

    private var myItemClickListener: MyItemClickListener? = null
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = getItem(position)
        holder.bind(diary)

        holder.itemView.setOnClickListener {
            myItemClickListener?.onItemClick(diary)
        }
    }

    inner class ViewHolder(private val binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) {
            binding.tvDiaryContent.text = diary.content
            binding.tvTime.text = diary.updatedAt.toTimeString()

            // 감정 레벨이 있으면 이미지 표시, 없으면 숨김
            if (diary.emotionLevel != null) {
                val emotionImageRes = getEmotionImageRes(diary.emotionLevel)
                binding.ivEmotion.setImageResource(emotionImageRes)
                binding.ivEmotion.isVisible = true
            } else {
                binding.ivEmotion.isVisible = false
            }

            // 타입 순서 정의
            val typeOrder = listOf(DiaryType.EMOTION, DiaryType.WORRY, DiaryType.PRAISE, DiaryType.THANKS)

            // 각 TextView 맵핑
            val typeToTextView = mapOf(
                DiaryType.EMOTION to binding.tvEmotion,
                DiaryType.WORRY to binding.tvWorry,
                DiaryType.PRAISE to binding.tvPraise,
                DiaryType.THANKS to binding.tvThanks
            )

            // 모든 TextView 숨김
            typeToTextView.values.forEach { it.visibility = View.GONE }

            // Diary에 있는 타입만 보여주기, 순서 유지
            typeOrder.forEach { type ->
                if (diary.types.contains(type)) {
                    typeToTextView[type]?.visibility = View.VISIBLE
                }
            }
        }
        
        /**
         * 감정 레벨 (1~5)을 UI 이미지 리소스로 변환
         */
        @DrawableRes
        private fun getEmotionImageRes(emotionLevel: Int): Int {
            return when (emotionLevel) {
                1 -> R.drawable.img_emotion_very_sad
                2 -> R.drawable.img_emotion_sad
                3 -> R.drawable.img_emotion_neutral
                4 -> R.drawable.img_emotion_happy
                5 -> R.drawable.img_emotion_very_happy
                else -> R.drawable.img_emotion_neutral // 기본값
            }
        }
    }

    class DiaryDiffCallback : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem.id == newItem.id // ID 기준으로 같은 아이템인지 판단
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem // 내용까지 동일한지 판단
        }
    }
}
