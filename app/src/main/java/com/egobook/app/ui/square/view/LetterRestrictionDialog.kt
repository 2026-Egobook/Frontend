package com.egobook.app.ui.square.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogLetterRestrictionBinding
import com.egobook.app.removeScreenBlur
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Uses the same background, fonts, colours and button style as the account dialogs. */
class LetterRestrictionDialog : DialogFragment(R.layout.dialog_letter_restriction) {
    override fun onStart() {
        super.onStart()
        val density = resources.displayMetrics.density
        val width = minOf((233 * density).toInt(), resources.displayMetrics.widthPixels - (32 * density).toInt())
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(android.view.Gravity.CENTER)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = DialogLetterRestrictionBinding.bind(view)
        val reason = arguments?.getString("reason")
        val until = arguments?.getString("until")
        val date = until?.let { timestamp ->
            runCatching { OffsetDateTime.parse(timestamp).atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime() }
                .recoverCatching { LocalDateTime.parse(timestamp) }.getOrNull()
        }?.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")) ?: until
        val reasonText = reason?.let { getString(R.string.letter_restriction_reason, it.replace(" (", "\n(")) }.orEmpty()
        val dateText = date?.let { getString(R.string.letter_restriction_until, it) }.orEmpty()
        binding.tvRestrictionReason.text = reasonText
        binding.tvRestrictionReason.isVisible = reasonText.isNotBlank()
        binding.tvRestrictionUntil.text = dateText
        binding.tvRestrictionUntil.isVisible = dateText.isNotBlank()
        binding.btnRestrictionConfirm.setOnClickListener { dismiss() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        removeScreenBlur()
    }

    companion object {
        const val TAG = "LetterRestrictionDialog"
        fun newInstance(reason: String?, until: String?) = LetterRestrictionDialog().apply {
            arguments = bundleOf("reason" to reason, "until" to until)
        }
    }
}
