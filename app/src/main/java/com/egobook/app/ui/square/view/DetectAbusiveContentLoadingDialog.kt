package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogDetectAbusiveContentLoadingBinding

class DetectAbusiveContentLoadingDialog: DialogFragment(R.layout.dialog_detect_abusive_content_loading) {
    private lateinit var binding: DialogDetectAbusiveContentLoadingBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDetectAbusiveContentLoadingBinding.bind(view)
    }

    companion object {
        const val TAG = "DetectAbusiveContentLoadingDialog"
    }
}