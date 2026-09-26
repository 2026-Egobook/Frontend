package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.egobook.app.databinding.DialogSupportBinding
import com.egobook.app.removeScreenBlur

class SupportThanksDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "SupportThanksDialogFragment"
    }

    private var _binding: DialogSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        _binding = DialogSupportBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        removeScreenBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}