package com.egobook.app.ui.diary.view

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.egobook.app.R
import com.egobook.app.databinding.FragmentDiaryExportDialogBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
import com.egobook.app.ui.diary.viewmodel.TermType
import kotlinx.coroutines.launch

class DiaryExportDialogFragment : DialogFragment() {

    private var _binding: FragmentDiaryExportDialogBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: DiariesViewModel by activityViewModels()

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
        setupObservers()
        setClickListener()
        updateButtonState()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.isValidDate.collect { state ->
                when (state) {
                    TermType.StartFuture -> {
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.critical)
                        )
                        binding.tvLastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icEndDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.tvExportGuide.setText("미래 날짜는 내보낼 수 없어요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                    }
                    TermType.EndFuture -> {
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.tvLastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                        binding.icEndDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.critical)
                        )
                        binding.tvExportGuide.setText("미래 날짜는 내보낼 수 없어요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                    }
                    TermType.Reverse -> {
                        // 시작 날짜가 종료 날짜보다 늦을 경우 빨간색으로 표시
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.critical)
                        )
                        binding.btnPdf.isEnabled = false
                        binding.btnText.isEnabled = false
                        binding.tvExportGuide.setText("시작 날짜가 끝 날짜보다 이전이거나\n" +
                                "같아야 해요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                    }
                    TermType.MoreThanOneYear -> {
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.critical)
                        )
                        binding.tvLastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                        binding.icEndDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.critical)
                        )
                        binding.tvExportGuide.setText("최대 1년 단위로 끊어서 내보낼 수 있어요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical))
                    }
                    TermType.Valid -> {
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.tvLastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icEndDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.btnPdf.isEnabled = true
                        binding.btnText.isEnabled = true
                        binding.tvExportGuide.setText("최대 1년 단위로 내보낼 수 있어요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                    }
                    TermType.None -> {
                        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icStartDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.tvLastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                        binding.icEndDate.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.neutral)
                        )
                        binding.btnPdf.isEnabled = false
                        binding.btnText.isEnabled = false
                        binding.tvExportGuide.setText("최대 1년 단위로 내보낼 수 있어요")
                        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral))
                    }
                }
            }
        }
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

        fun createDateWatcher(editText: EditText): TextWatcher {
            return object : TextWatcher {
                private var isFormatting = false

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting || s == null) return

                    isFormatting = true

                    val original = s.toString()
                    val cursorPosition = editText.selectionStart

                    val digits = original.replace(".", "").take(8)

                    // 커서를 digit 기준으로 변환
                    val digitsBeforeCursor = original
                        .substring(0, cursorPosition)
                        .count { it.isDigit() }

                    val formatted = StringBuilder()
                    var newCursor = 0

                    for (i in digits.indices) {
                        if (i == 4 || i == 6) {
                            formatted.append('.')
                        }
                        formatted.append(digits[i])

                        // cursor 위치 계산 (정확한 방식)
                        if (i < digitsBeforeCursor) {
                            newCursor = formatted.length
                        }
                    }

                    s.replace(0, s.length, formatted.toString())

                    try {
                        editText.setSelection(newCursor)
                    } catch (e: Exception) {
                        editText.setSelection(formatted.length)
                    }

                    isFormatting = false
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        }

        // watcher 각각 따로 붙이기
        binding.tvStartDate.addTextChangedListener(createDateWatcher(binding.tvStartDate))
        binding.tvLastDate.addTextChangedListener(createDateWatcher(binding.tvLastDate))

        // validation
        binding.tvStartDate.doAfterTextChanged { updateButtonState() }
        binding.tvLastDate.doAfterTextChanged { updateButtonState() }
    }

    private fun updateButtonState() {
        val startDate = binding.tvStartDate.text.toString()
        val lastDate = binding.tvLastDate.text.toString()
        
        // 뷰모델에서 날짜 선후 관계 및 형식 검사 수행
        viewmodel.validateDates(startDate, lastDate)
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
