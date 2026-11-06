package com.egobook.app.ui.home.ui

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.egobook.app.R
import com.egobook.app.databinding.DialogRecallDiaryBinding
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.diary.util.emotionImageRes
import com.egobook.app.ui.util.toTimeString
import java.time.LocalDateTime

/**
 * 과거의 나 돌아보기 팝업.
 *
 * 버튼 선택 결과는 [RESULT_KEY]로 전달한다. ([ACTION_VIEW]: 해당 일기 보러 가기, [ACTION_WRITE]: 오늘 일기 쓰러 가기)
 */
class RecallDiaryDialog : DialogFragment(R.layout.dialog_recall_diary) {
    override fun onStart() {
        super.onStart()
        val density = resources.displayMetrics.density
        val width = minOf((312 * density).toInt(), resources.displayMetrics.widthPixels - (32 * density).toInt())
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.CENTER)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = DialogRecallDiaryBinding.bind(view)
        val args = requireArguments()
        val diaryId = args.getLong(ARG_DIARY_ID)
        val emotionLevel = args.getInt(ARG_EMOTION_LEVEL, NO_EMOTION_LEVEL)
        val types =
            args
                .getStringArrayList(ARG_TYPES)
                .orEmpty()
                .mapNotNull { runCatching { DiaryType.valueOf(it) }.getOrNull() }
                .toSet()

        binding.tvTime.text =
            args
                .getString(ARG_WRITTEN_AT)
                ?.let { runCatching { LocalDateTime.parse(it).toTimeString() }.getOrNull() }
                .orEmpty()
        binding.tvDiaryContent.text = args.getString(ARG_CONTENT).orEmpty()

        binding.ivEmotion.isVisible = emotionLevel != NO_EMOTION_LEVEL
        if (emotionLevel != NO_EMOTION_LEVEL) {
            binding.ivEmotion.setImageResource(emotionImageRes(emotionLevel))
        }

        mapOf(
            DiaryType.EMOTION to binding.tvEmotion,
            DiaryType.CONCERN to binding.tvWorry,
            DiaryType.PRAISE to binding.tvPraise,
            DiaryType.GRATITUDE to binding.tvThanks,
        ).forEach { (type, textView) -> textView.isVisible = type in types }

        binding.ivRecallClose.setOnClickListener { dismiss() }
        binding.btnRecallView.setOnClickListener { sendResult(ACTION_VIEW, diaryId) }
        binding.btnRecallWrite.setOnClickListener { sendResult(ACTION_WRITE, diaryId) }
    }

    private fun sendResult(
        action: String,
        diaryId: Long,
    ) {
        setFragmentResult(RESULT_KEY, bundleOf(RESULT_ACTION to action, RESULT_DIARY_ID to diaryId))
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        removeScreenBlur()
    }

    companion object {
        const val TAG = "RecallDiaryDialog"
        const val RESULT_KEY = "recall_diary_result"
        const val RESULT_ACTION = "action"
        const val RESULT_DIARY_ID = "diaryId"
        const val ACTION_VIEW = "view"
        const val ACTION_WRITE = "write"

        private const val ARG_DIARY_ID = "diaryId"
        private const val ARG_WRITTEN_AT = "writtenAt"
        private const val ARG_TYPES = "types"
        private const val ARG_EMOTION_LEVEL = "emotionLevel"
        private const val ARG_CONTENT = "content"
        private const val NO_EMOTION_LEVEL = -1

        fun newInstance(diary: Diary) =
            RecallDiaryDialog().apply {
                arguments =
                    bundleOf(
                        ARG_DIARY_ID to diary.diaryId,
                        ARG_WRITTEN_AT to diary.writtenAt.toString(),
                        ARG_TYPES to ArrayList(diary.types.map { it.name }),
                        ARG_EMOTION_LEVEL to (diary.emotionLevel ?: NO_EMOTION_LEVEL),
                        ARG_CONTENT to diary.content,
                    )
            }
    }
}
