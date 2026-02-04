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
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.DialogLetterSendBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.ui.square.model.friend.FriendModel
import com.egobook.app.ui.square.model.letter.AbusiveContentModel
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.ui.square.model.letter.SendLetterModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class LetterSendDialog(private val mode: LetterMode, private val friendInfo: FriendModel? = null, private val letterContent: String, private val letterColor: LetterBackgroundColor): DialogFragment(R.layout.dialog_letter_send) {

    private lateinit var binding: DialogLetterSendBinding
    private val viewModel: LetterViewModel by activityViewModels()
    private var loadingDialog: DetectAbusiveContentLoadingDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogLetterSendBinding.bind(view)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() = with(binding) {
        when(mode) {
            LetterMode.FRIEND -> {
                tvLetterSendTitle.text = "${friendInfo?.name}에게\n편지를 보낼까요?"
                tvLetterSendDescription.text = "상대에게 내 이름이 보여요"
            }
            LetterMode.RANDOM -> {
                tvLetterSendTitle.text = "누군가에게\n편지를 보낼까요?"
                tvLetterSendDescription.text = "익명으로 전달돼요"
            }
        }
    }

    private fun initListeners() = with(binding) {
        btnLetterSendBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        btnLetterSendApply.setOnClickListener {
            viewModel.detectAbusiveContent(text = letterContent)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detectAbusiveContentResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {
                                dismiss() // 얘가 중요하다
                                showLoadingDialog()
                            }
                            is UiState.Success<AbusiveContentModel> -> {
                                hideLoadingDialog()
                                val abusiveContent = state.data
                                if(abusiveContent.isHarmful && abusiveContent.riskScore >= 80.0f) {
                                    val dialog = DetectAbusiveContentFailureDialog(originalContent = abusiveContent.text, badWords = abusiveContent.detectedBadWords).apply {
                                        isCancelable = false
                                    }
                                    dialog.show(parentFragmentManager, DetectAbusiveContentFailureDialog.TAG)
                                    applyScreenBlur(BlurLevel.BASE)
                                } else {
                                    val letter = SendLetterModel(
                                        mode = mode,
                                        receiverId = friendInfo?.id,
                                        content = letterContent,
                                        letterColor = letterColor
                                    )
                                    viewModel.sendLetter(letter = letter)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.sendLetterResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                val dialog = DetectAbusiveContentSuccessDialog().apply {
                                    isCancelable = false
                                }
                                dialog.show(parentFragmentManager, DetectAbusiveContentSuccessDialog.TAG)
                                applyScreenBlur(BlurLevel.BASE)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingDialog() {
        if(loadingDialog == null) {
            loadingDialog = DetectAbusiveContentLoadingDialog().apply {
                isCancelable = false
            }
            loadingDialog?.show(parentFragmentManager, DetectAbusiveContentLoadingDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
        removeScreenBlur()
    }

    companion object {
        const val TAG = "LetterSendDialog"
    }
}