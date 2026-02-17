package com.egobook.app.ui.square.view

import android.app.Dialog
import android.os.Bundle
import android.graphics.Color
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.DialogArrivedPendingLetterBinding
import com.egobook.app.domain.model.square.ReportOrigin
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterItemModel
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ArrivedPendingLetterDialog(
    private val letterInfo: ArrivedPendingLetterItemModel
): DialogFragment(R.layout.dialog_arrived_pending_letter) {
    private lateinit var binding: DialogArrivedPendingLetterBinding
    private val viewModel: LetterViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogArrivedPendingLetterBinding.bind(view)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() = with(binding) {
        tvArrivedPendingLetterArrivedAt.text = formatArrivedDateTime(dateTimeStr = letterInfo.arrivedAt)
        tvArrivedPendingLetterContent.text = letterInfo.content
        tvArrivedPendingLetterFromLabel.text = "From ${letterInfo.fromLabel}"
        cvArrivedPendingLetterContainer.backgroundTintList =
            when(letterInfo.backgroundColor) {
                LetterBackgroundColor.WHITE -> resources.getColorStateList(R.color.letter_bg_beige, null)
                LetterBackgroundColor.PINK -> resources.getColorStateList(R.color.letter_bg_pink, null)
                LetterBackgroundColor.GREEN -> resources.getColorStateList(R.color.letter_bg_green, null)
                LetterBackgroundColor.BLUE -> resources.getColorStateList(R.color.letter_bg_blue, null)
                LetterBackgroundColor.PURPLE -> resources.getColorStateList(R.color.letter_bg_purple, null)
            }
    }

    private fun initListeners() = with(binding) {
        ivArrivedPendingLetterDefer.setOnClickListener {
            viewModel.deferReplyLetter(letterId = letterInfo.letterId)
        }
        ivArrivedPendingLetterReport.setOnClickListener {
            val dialog = SquareReportDialog(origin = ReportOrigin.LETTER_ARRIVED, letterId = letterInfo.letterId).apply { isCancelable = false }
            dialog.show(childFragmentManager, SquareReportDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
        }
        btnArrivedPendingLetterGiveUp.setOnClickListener {
            dismiss()
            val dialog = GiveUpReplyLetterPopupDialog(letterInfo = letterInfo).apply { isCancelable = false }
            dialog.show(parentFragmentManager, GiveUpReplyLetterPopupDialog.TAG)
        }
        btnArrivedPendingLetterReply.setOnClickListener {
            dismiss()
            removeScreenBlur()
            val action = SquareFragmentDirections.actionMenuSquareToLetterReplyFragment(letterItem = letterInfo)
            findNavController().navigate(action)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deferReplyLetterResult.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<Unit> -> {
                            removeScreenBlur()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    /**
     * "arrivedAt": "2026-01-04T10:00:00+09:00" → 2026.01.04 로 변환해주는 메소드
     */
    private fun formatArrivedDateTime(dateTimeStr: String): String {
        val parsedDateTime: OffsetDateTime = OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return parsedDateTime.format(formatter)
    }

    companion object {
        const val TAG = "ArrivedPendingLetterDialog"
    }
}