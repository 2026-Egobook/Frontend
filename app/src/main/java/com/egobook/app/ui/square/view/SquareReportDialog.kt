package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogSquareReportBinding
import com.egobook.app.removeScreenBlur

class SquareReportDialog: DialogFragment(R.layout.dialog_square_report) {
    private lateinit var binding: DialogSquareReportBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSquareReportBinding.bind(view)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnSquareReportBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    companion object {
        val TAG = "SquareReportDialog"
    }
}