package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogPremiumLetterBuyBinding
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.counseling.view.WeeklyReportUnlockDialog.Companion.INK_PRICE
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class PremiumLetterBuyDialog(private val letterColor: LetterBackgroundColor) :
    DialogFragment(R.layout.dialog_premium_letter_buy) {
    private lateinit var binding: DialogPremiumLetterBuyBinding
    private val viewModel: LetterViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogPremiumLetterBuyBinding.bind(view)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() = with(binding) {
        val backgroundColor = when (letterColor) {
            LetterBackgroundColor.PINK -> R.color.letter_bg_pink
            LetterBackgroundColor.GREEN -> R.color.letter_bg_green
            LetterBackgroundColor.BLUE -> R.color.letter_bg_blue
            else -> R.color.letter_bg_purple
        }
        val letterBanner = when (letterColor) {
            LetterBackgroundColor.PINK -> R.drawable.img_letter_pink
            LetterBackgroundColor.GREEN -> R.drawable.img_letter_green
            LetterBackgroundColor.BLUE -> R.drawable.img_letter_blue
            else -> R.drawable.img_letter_purple
        }
        cvPremiumLetterColor.backgroundTintList = resources.getColorStateList(backgroundColor, null)
        ivPremiumLetterBanner.setImageResource(letterBanner)
    }

    private fun initListeners() = with(binding) {
        btnPremiumLetterBack.setOnClickListener {
            dismiss()
            removeScreenBlur()
        }
        btnPremiumLetterBuy.setOnClickListener {
            viewModel.getUserInfo()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfo.collect { state ->
                    when (state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<User> -> {
                            val userInfo = state.data
                            val currentInk = userInfo.ink.value
                            if (currentInk >= LETTER_PRICE) {
                                Toast.makeText(context, "서비스 점검 중입니다!", Toast.LENGTH_SHORT).show()
                                // TODO: 편지 잉크 구매 API 연동하기
                            } else {
                                Toast.makeText(context, "현재 잉크가 부족합니다!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }
        }
    }

    companion object {
        const val TAG = "PremiumLetterBuyDialog"
        private const val LETTER_PRICE = 500
    }
}