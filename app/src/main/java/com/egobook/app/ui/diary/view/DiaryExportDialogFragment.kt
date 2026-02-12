package com.egobook.app.ui.diary.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.egobook.app.databinding.FragmentDiaryExportDialogBinding
import com.egobook.app.removeScreenBlur

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
        setClickListener()
        updateButtonState() // 초기 버튼 상태 설정

    }

    private fun setClickListener() {
        binding.btnPdf.setOnClickListener {
            Toast.makeText(requireContext(), "내보내기 기능은 준비중입니다!", Toast.LENGTH_SHORT).show()
        }
        binding.btnText.setOnClickListener {
            Toast.makeText(requireContext(), "내보내기 기능은 준비중입니다!", Toast.LENGTH_SHORT).show()
        }
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

        // 각 EditText에 TextWatcher를 적용.
        binding.tvStartDate.addTextChangedListener(dateWatcher)
        binding.tvLastDate.addTextChangedListener(dateWatcher)

        // 날짜 유효성 검사를 위한 TextWatcher
        binding.tvStartDate.doAfterTextChanged { updateButtonState() }
        binding.tvLastDate.doAfterTextChanged { updateButtonState() }
    }

    /**
     * 날짜 입력 유효성 검사 후 버튼 상태 업데이트
     * YYYY.MM.DD 형식(10자리)이 모두 입력되었을 때만 버튼 활성화
     */
    private fun updateButtonState() {
        val startDate = binding.tvStartDate.text.toString()
        val lastDate = binding.tvLastDate.text.toString()

        // YYYY.MM.DD 형식 확인 (10자리)
        val isStartDateValid = startDate.length == 10 && isValidDateFormat(startDate)
        val isLastDateValid = lastDate.length == 10 && isValidDateFormat(lastDate)

        val isBothDatesValid = isStartDateValid && isLastDateValid

        binding.btnPdf.isEnabled = isBothDatesValid
        binding.btnText.isEnabled = isBothDatesValid
    }

    /**
     * 날짜 형식 유효성 검사 (YYYY.MM.DD)
     */
    private fun isValidDateFormat(date: String): Boolean {
        // 정규식: YYYY.MM.DD 형식
        val datePattern = Regex("""\d{4}\.\d{2}\.\d{2}""")
        return datePattern.matches(date)
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
