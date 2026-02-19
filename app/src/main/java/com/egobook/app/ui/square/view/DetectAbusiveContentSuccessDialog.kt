package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.DialogDetectAbusiveContentSuccessBinding
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.domain.model.square.letter.ReplyReward
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.letter.ReplyLetterModel

class DetectAbusiveContentSuccessDialog(private val status: LetterStatus, private val replyItem: ReplyLetterModel? = null): DialogFragment(R.layout.dialog_detect_abusive_content_success) {
    private lateinit var binding: DialogDetectAbusiveContentSuccessBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDetectAbusiveContentSuccessBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        when(status) {
            LetterStatus.SENT -> {
                btnDetectAbusiveSuccess.text = "잉크 1 획득!"
            }
            LetterStatus.REPLIED -> {
                var rewardText: String? = null
                replyItem?.rewards?.forEach { reward ->
                    if(reward.kind == ReplyReward.INK) {
                        rewardText = "${ReplyReward.INK.label} ${reward.amount} 획득!"
                        return@forEach
                    }
                }
                btnDetectAbusiveSuccess.text = rewardText ?: "확인"
            }
            else -> {}
        }
    }

    private fun initListeners() = with(binding) {
        btnDetectAbusiveSuccess.setOnClickListener {
            dismiss()
            removeScreenBlur()
            findNavController().popBackStack()
        }
    }

    companion object {
        const val TAG = "DetectAbusiveContentSuccessDialog"
    }
}