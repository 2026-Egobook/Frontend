package com.egobook.app.ui.diary.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.databinding.ItemDiaryBinding
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.ui.util.toTimeString

class DiaryRVAdapter :
    PagingDataAdapter<DiarySummary, DiaryRVAdapter.ViewHolder>(DiaryDiffCallback()) {
    private val expandedDiaryIds = mutableSetOf<Long>()

    interface MyItemClickListener {
        fun onItemClick(diary: DiarySummary)
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
        diary?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                myItemClickListener?.onItemClick(diary)
            }
        }
    }

    inner class ViewHolder(private val binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: DiarySummary) {
            binding.tvDiaryContent.text = diary.content
            binding.tvTime.text = diary.writtenAt.toTimeString()
            applyContentExpandedState(expandedDiaryIds.contains(diary.diaryId))

            binding.tvDiaryContent.setOnClickListener {
                toggleContentExpandedState(diary.diaryId)
            }

            // 감정 레벨이 있으면 이미지 표시, 없으면 숨김
            if (diary.emotionLevel != null) {
                val emotionImageRes = getEmotionImageRes(diary.emotionLevel)
                binding.ivEmotion.setImageResource(emotionImageRes)
                binding.ivEmotion.isVisible = true
            } else {
                binding.ivEmotion.isVisible = false
            }

            // 타입 순서 정의
            val typeOrder = listOf(DiaryType.EMOTION, DiaryType.CONCERN, DiaryType.PRAISE, DiaryType.GRATITUDE)

            // 각 TextView 맵핑
            val typeToTextView = mapOf(
                DiaryType.EMOTION to binding.tvEmotion,
                DiaryType.CONCERN to binding.tvWorry,
                DiaryType.PRAISE to binding.tvPraise,
                DiaryType.GRATITUDE to binding.tvThanks
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

        private fun applyContentExpandedState(isExpanded: Boolean) = with(binding.tvDiaryContent) {
            maxLines = if (isExpanded) Int.MAX_VALUE else COLLAPSED_CONTENT_MAX_LINES
            ellipsize = if (isExpanded) null else TextUtils.TruncateAt.END
        }

        private fun toggleContentExpandedState(diaryId: Long) {
            val isExpanded = if (expandedDiaryIds.contains(diaryId)) {
                expandedDiaryIds.remove(diaryId)
                false
            } else {
                expandedDiaryIds.add(diaryId)
                true
            }
            applyContentExpandedState(isExpanded)
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

    class DiaryDiffCallback : DiffUtil.ItemCallback<DiarySummary>() {
        override fun areItemsTheSame(oldItem: DiarySummary, newItem: DiarySummary): Boolean {
            return oldItem.diaryId == newItem.diaryId // ID 기준으로 같은 아이템인지 판단
        }

        override fun areContentsTheSame(oldItem: DiarySummary, newItem: DiarySummary): Boolean {
            return oldItem == newItem // 내용까지 동일한지 판단
        }
    }

    companion object {
        private const val COLLAPSED_CONTENT_MAX_LINES = 3
    }
}
