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
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.FragmentAccountDeleteDialog1Binding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import kotlinx.coroutines.launch

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
        observeAccountType()
    }

    private fun observeAccountType() {
        val linkedWarning = binding.tvDeleteWarning.text
        val linkedWarningMargin =
            (binding.tvDeleteWarning.layoutParams as ViewGroup.MarginLayoutParams).topMargin
        binding.tvRecoveryDescription.isVisible = false
        binding.tvDeleteWarning.isVisible = false
        binding.btnRealDelete.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isGoogleLinked.collect { isLinked ->
                    if (isLinked == null) return@collect
                    binding.tvRecoveryDescription.isVisible = isLinked
                    binding.tvDeleteWarning.isVisible = true
                    binding.tvDeleteWarning.text = if (isLinked) linkedWarning
                        else getString(R.string.withdraw_guest_warning)
                    binding.tvDeleteWarning.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        topMargin = if (isLinked) linkedWarningMargin
                            else (16 * resources.displayMetrics.density).toInt()
                    }
                    binding.btnRealDelete.isEnabled = true
                }
            }
        }
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
