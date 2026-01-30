package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogSquareReportBinding
import com.egobook.app.removeScreenBlur

class SquareReportDialog: DialogFragment(R.layout.dialog_square_report) {
    private lateinit var binding: DialogSquareReportBinding
    private val clickContentList by lazy {
        listOf(binding.tvSquareReportAbuse, binding.tvSquareReportSpam, binding.tvSquareReportInappropriate, binding.tvSquareReportEtcNotClicked)
    }

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
        clickContentList.forEach { content ->
            content.setOnClickListener {
                clickContentList.forEach { it.isSelected = false }
                content.isSelected = true
                if(tvSquareReportEtcNotClicked.isSelected) {
                    llSquareReportEtcClicked.isVisible = true
                    tvSquareReportEtcNotClicked.isVisible = false
                } else {
                    llSquareReportEtcClicked.isVisible = false
                    tvSquareReportEtcNotClicked.isVisible = true
                }
            }
        }
        etSquareReportEtcReason.addTextChangedListener(object: TextWatcher {
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
    }

    companion object {
        val TAG = "SquareReportDialog"
    }
}