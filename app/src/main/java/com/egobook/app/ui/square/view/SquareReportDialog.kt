package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogSquareReportBinding
import com.egobook.app.domain.model.square.letter.ReportLetterType
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.letter.ReportLetterModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class SquareReportDialog(private val letterId: Long, private val replyId: Long) :
    DialogFragment(R.layout.dialog_square_report) {
    private lateinit var binding: DialogSquareReportBinding
    private val viewModel: LetterViewModel by activityViewModels()
    private val reportReasonsWithoutEtc by lazy {
        listOf(
            binding.tvSquareReportAbuse,
            binding.tvSquareReportSpam,
            binding.tvSquareReportInappropriate
        )
    }
    private lateinit var reportType: ReportLetterType

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSquareReportBinding.bind(view)
        initListeners()
        initObservers()
    }

    private fun initListeners() = with(binding) {
        btnSquareReportBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        reportReasonsWithoutEtc.forEach { content ->
            content.setOnClickListener {
                reportReasonsWithoutEtc.forEach { it.isSelected = false }
                tvSquareReportEtcNotClicked.isSelected = false
                llSquareReportEtcClicked.isVisible = false
                tvSquareReportEtcNotClicked.isVisible = true
                content.isSelected = true
                btnSquareReportSubmit.isEnabled = true
                reportType = when(content) {
                    tvSquareReportAbuse -> ReportLetterType.ABUSE
                    tvSquareReportSpam -> ReportLetterType.SPAM
                    else -> ReportLetterType.INAPPROPRIATE
                }
            }
        }
        tvSquareReportEtcNotClicked.setOnClickListener {
            reportReasonsWithoutEtc.forEach { it.isSelected = false }
            it.isSelected = true
            llSquareReportEtcClicked.isVisible = true
            tvSquareReportEtcNotClicked.isVisible = false
            btnSquareReportSubmit.isEnabled = !etSquareReportEtcReason.text.isNullOrBlank()
            reportType = ReportLetterType.OTHER
        }
        etSquareReportEtcReason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) = Unit

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                btnSquareReportSubmit.isEnabled = !text.isNullOrBlank()
            }
        })
        btnSquareReportSubmit.setOnClickListener {
            viewModel.reportRepliedLetter(replyId = replyId, reportLetter = ReportLetterModel(
                reason = reportType,
                description = if(reportType == ReportLetterType.OTHER) etSquareReportEtcReason.text.toString() else null
            ))
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reportRepliedLetterResult.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<Unit> -> {
                            Toast.makeText(context, "답장이 신고되었습니다.", Toast.LENGTH_SHORT).show()
                            viewModel.getSentLetterWithReply(letterId = letterId)
                            removeScreenBlur()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    companion object {
        val TAG = "SquareReportDialog"
    }
}