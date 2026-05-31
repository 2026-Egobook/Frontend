package com.egobook.app.ui.account.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.databinding.FragmentNicknameEditDialogBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NicknameEditDialogFragment : DialogFragment() {

    private var _binding: FragmentNicknameEditDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels({ requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onStart() {
        super.onStart()
        val density = resources.displayMetrics.density
        val horizontalMarginPx = (24 * density).toInt()
        dialog?.window?.apply {
            val screenWidth = context.resources.displayMetrics.widthPixels
            setLayout(screenWidth - horizontalMarginPx * 2, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNicknameEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefillCurrentNickname()
        setClickListeners()
        observeNicknameUpdateState()
    }

    private fun prefillCurrentNickname() {
        val current = viewModel.nickname.value
        if (!current.isNullOrBlank()) {
            binding.etNickname.setText(current)
        }
    }

    private fun setClickListeners() {
        binding.btnBack.setOnClickListener { dismiss() }

        binding.btnConfirm.setOnClickListener {
            val input = binding.etNickname.text?.toString()?.trim() ?: ""
            viewModel.updateNickname(input)
        }
    }

    private fun observeNicknameUpdateState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nicknameUpdateState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.btnConfirm.isEnabled = false
                        is UiState.Success -> {
                            viewModel.resetNicknameUpdateState()
                            dismiss()
                        }
                        else -> binding.btnConfirm.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        removeScreenBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NicknameEditDialog"
    }
}
