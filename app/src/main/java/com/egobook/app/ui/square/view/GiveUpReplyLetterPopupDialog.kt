package com.egobook.app.ui.square.view


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogGiveUpReplyLetterBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterItemModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch
import kotlin.getValue

class GiveUpReplyLetterPopupDialog(
    private val letterInfo: ArrivedPendingLetterItemModel
): DialogFragment(R.layout.dialog_give_up_reply_letter) {
    private lateinit var binding: DialogGiveUpReplyLetterBinding
    private val viewModel: LetterViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogGiveUpReplyLetterBinding.bind(view)
        initListeners()
        initObservers()
    }

    private fun initListeners() = with(binding) {
        btnGiveUpReplyLetterDefer.setOnClickListener {
            viewModel.deferReplyLetter(letterId = letterInfo.letterId)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deferReplyLetterResult.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<Unit> -> {
                            removeScreenBlur()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "GiveUpReplyLetterPopupDialog"
    }

}