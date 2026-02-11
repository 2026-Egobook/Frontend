package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogDetectAbusiveContentFailureBinding
import com.egobook.app.removeScreenBlur

class DetectAbusiveContentFailureDialog(
    private val originalContent: String,
    private val badWords: List<String>
): DialogFragment(R.layout.dialog_detect_abusive_content_failure) {
    private lateinit var binding: DialogDetectAbusiveContentFailureBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDetectAbusiveContentFailureBinding.bind(view)
        initViews()
        initListeners()
    }

    /**
     * 정규식으로 문장 분리
     * 문장 분리 후 앞뒤 공백 제거
     * 문장이 채워져있고, badWords 중에 하나라도 포함하는 경우만 필터링
     * 검열된 문장 리스트를 다시 하나의 문장으로 잇기
     */
    private fun initViews() = with(binding) {
        val splitSentences: List<String> = originalContent.split(Regex("[.?!\n]]"))
        val trimSentences: List<String> = splitSentences.map { it.trim() }
        val filteredSentences: List<String> = trimSentences.filter { sentence -> sentence.isNotEmpty() && badWords.any { badWord -> sentence.contains(badWord) } }
        tvDetectAbusiveContentFailureWordsDescription.text = filteredSentences.joinToString(", ")
    }

    private fun initListeners() = with(binding) {
        btnDetectAbusiveContentFailureEdit.setOnClickListener {
            dismiss()
            removeScreenBlur()
        }
    }

    companion object {
        const val TAG = "DetectAbusiveContentFailureDialog"
    }
}