package com.example.egobook_frontent.ui.diary.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.egobook_frontent.databinding.FragmentDiaryExportDialogBinding
import com.example.egobook_frontent.removeScreenBlur

class DiaryExportDialogFragment : DialogFragment() {

    private var _binding: FragmentDiaryExportDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentDiaryExportDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateInputWatchers()

    }

    private fun setupDateInputWatchers() {
        // 년/월/일 입력 시 자동으로 "."을 추가해주는 TextWatcher
        val dateWatcher = object : TextWatcher {
            private var isFormatting = false // 무한 루프 방지 플래그
            private var beforeTextLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeTextLength = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                val currentLength = s?.length ?: 0
                // 사용자가 글자를 추가했을 때만 동작
                if (currentLength > beforeTextLength) {
                    if (currentLength == 4 || currentLength == 7) {
                        isFormatting = true
                        s?.append('.')
                        isFormatting = false
                    }
                }
            }
        }

        // 각 EditText에 TextWatcher를 적용합니다.
        binding.tvStartDate.addTextChangedListener(dateWatcher)
        binding.tvLastDate.addTextChangedListener(dateWatcher)
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        removeScreenBlur()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
