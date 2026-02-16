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
                val rewardList = mutableListOf<String>()
                replyItem?.rewards?.forEach { reward ->
                    when(reward.kind) {
                        ReplyReward.INK -> {
                            rewardList.add("${ReplyReward.INK.label} ${reward.amount}") // 잉크 1
                        }
                        ReplyReward.EMPATHY -> {
                            rewardList.add("${ReplyReward.EMPATHY.label} ${reward.amount}") // 공감성 1
                        }
                    }
                }
                val totalRewards = rewardList.joinToString(", ") // 잉크 1, 공감성 1
                btnDetectAbusiveSuccess.text = "$totalRewards 획득!" // 잉크 1, 공감성 1 획득!
            }
            else -> {}
        }
    }

    private fun initListeners() = with(binding) {
        btnDetectAbusiveSuccess.setOnClickListener {
            removeScreenBlur()
            findNavController().popBackStack()
        }
    }

    companion object {
        const val TAG = "DetectAbusiveContentSuccessDialog"
    }
}