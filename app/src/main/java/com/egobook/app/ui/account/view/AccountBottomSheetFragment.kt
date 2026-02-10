package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egobook.app.databinding.FragmentAccountBottomSheetBinding
import com.egobook.app.ui.account.view.AccountFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAccountBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? AccountFragment)?.clearBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AccountBottomSheet"
    }
}