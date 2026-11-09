package com.egobook.app.ui.onboarding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogServerShutdownBinding

class ServerShutdownDialogFragment : DialogFragment() {

    private var _binding: DialogServerShutdownBinding? = null
    private val binding get() = _binding!!

    var onConfirm: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogServerShutdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false  // 바깥/뒤로가기로 안 닫히게, 확인으로만 닫힘

        binding.tvShutdownDescription.text = HtmlCompat.fromHtml(
            getString(R.string.dialog_shutdown_description),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.btnShutdownConfirm.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object { const val TAG = "ServerShutdownDialog" }
}