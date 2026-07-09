package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogReplyDeleteConfirmBinding
import com.egobook.app.removeScreenBlur

class ReplyDeleteConfirmDialog(
    private val onConfirmDelete: () -> Unit
) : DialogFragment(R.layout.dialog_reply_delete_confirm) {

    private lateinit var binding: DialogReplyDeleteConfirmBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogReplyDeleteConfirmBinding.bind(view)

        binding.btnReplyDeleteCancel.setOnClickListener {
            dismiss()
            removeScreenBlur()
        }

        binding.btnReplyDeleteConfirm.setOnClickListener {
            onConfirmDelete()
            dismiss()
            removeScreenBlur()
        }
    }

    companion object {
        const val TAG = "ReplyDeleteConfirmDialog"
    }
}