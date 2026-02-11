package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogPremiumLetterBuyBinding
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.removeScreenBlur

class PremiumLetterBuyDialog(private val letterColor: LetterBackgroundColor): DialogFragment(R.layout.dialog_premium_letter_buy) {
    private lateinit var binding: DialogPremiumLetterBuyBinding

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
    }

    private fun initViews() = with(binding) {
        val backgroundColor = when(letterColor) {
            LetterBackgroundColor.PINK -> R.color.letter_bg_pink
            LetterBackgroundColor.GREEN -> R.color.letter_bg_green
            LetterBackgroundColor.BLUE -> R.color.letter_bg_blue
            else -> R.color.letter_bg_purple
        }
        val letterBanner = when(letterColor) {
            LetterBackgroundColor.PINK -> R.drawable.img_letter_pink
            LetterBackgroundColor.GREEN -> R.drawable.img_letter_green
            LetterBackgroundColor.BLUE -> R.drawable.img_letter_blue
            else -> R.drawable.img_letter_purple
        }
        cvPremiumLetterColor.backgroundTintList = resources.getColorStateList(backgroundColor,null)
        ivPremiumLetterBanner.setImageResource(letterBanner)
    }

    private fun initListeners() = with(binding) {
        btnPremiumLetterBack.setOnClickListener {
            dismiss()
            removeScreenBlur()
        }
        btnPremiumLetterBuy.setOnClickListener {
            // TODO: 유저 정보 연동 필요
        }
    }

    companion object {
        const val TAG = "PremiumLetterBuyDialog"
    }
}