package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogLetterSendBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.domain.model.square.letter.LetterMode

class LetterSendDialog(private val type: LetterMode): DialogFragment(R.layout.dialog_letter_send) {

    private lateinit var binding: DialogLetterSendBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogLetterSendBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        when(type) {
            LetterMode.FRIEND -> {
                tvLetterSendTitle.text = "친구이름에게\n편지를 보낼까요?"
                tvLetterSendDescription.text = "상대에게 내 이름이 보여요"
            }
            LetterMode.RANDOM -> {
                tvLetterSendTitle.text = "누군가에게\n편지를 보낼까요?"
                tvLetterSendDescription.text = "익명으로 전달돼요"
            }
        }
    }

    private fun initListeners() = with(binding) {
        btnLetterSendBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        btnLetterSendApply.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    companion object {
        const val TAG = "LetterSendDialog"
    }
}