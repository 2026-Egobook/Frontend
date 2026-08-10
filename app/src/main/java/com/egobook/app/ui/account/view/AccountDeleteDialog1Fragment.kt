package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.egobook.app.databinding.FragmentAccountDeleteDialog1Binding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.account.viewmodel.AccountViewModel

class AccountDeleteDialog1Fragment : DialogFragment() {

    private var _binding: FragmentAccountDeleteDialog1Binding? = null
    private val binding get() = _binding!!

    //부모 프래그먼트의 뷰모델 공유
    private val viewModel: AccountViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentAccountDeleteDialog1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        binding.btnRealDelete.setOnClickListener {
            navigateToDeleteReasonDialog()
        }
    }

    private fun navigateToDeleteReasonDialog() {
        viewModel.resetWithdrawReason()

        val reasonDialogFragment = AccountDeleteReasonDialogFragment()
        reasonDialogFragment.isCancelable = true
        reasonDialogFragment.show(parentFragmentManager, "AccountDeleteReasonDialogFragment")
        dismiss()
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
