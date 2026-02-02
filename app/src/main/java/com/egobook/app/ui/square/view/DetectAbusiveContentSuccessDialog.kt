package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.DialogDetectAbusiveContentSuccessBinding
import com.egobook.app.removeScreenBlur

class DetectAbusiveContentSuccessDialog: DialogFragment(R.layout.dialog_detect_abusive_content_success) {
    private lateinit var binding: DialogDetectAbusiveContentSuccessBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDetectAbusiveContentSuccessBinding.bind(view)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnDetectAbusiveSuccess.setOnClickListener {
            Toast.makeText(context, "잉크 1개를 획득하였습니다.", Toast.LENGTH_SHORT).show()
            removeScreenBlur()
            findNavController().popBackStack()
        }
    }

    companion object {
        const val TAG = "DetectAbusiveContentSuccessDialog"
    }
}