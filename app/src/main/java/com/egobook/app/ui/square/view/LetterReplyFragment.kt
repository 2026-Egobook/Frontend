package com.egobook.app.ui.square.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentLetterReplyBinding
import com.egobook.app.databinding.LayoutLetterTooltipPopupBinding
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor

class LetterReplyFragment : Fragment(R.layout.fragment_letter_reply) {
    private lateinit var binding: FragmentLetterReplyBinding
    private val letterItem by lazy {
        val args: LetterReplyFragmentArgs by navArgs()
        args.letterItem
    }
    private val premiumLetterColorList by lazy {
        listOf(binding.cvLetterReplyColorPink, binding.cvLetterReplyColorGreen, binding.cvLetterReplyColorBlue, binding.cvLetterReplyColorPurple)
    }

    private var letterColor: LetterBackgroundColor = LetterBackgroundColor.BEIGE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterReplyBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        tvLetterReplyReceivedContent.text = letterItem.content
        cvLetterReplyColorBeige.isSelected = true
        cvLetterReplyColorBeige.getChildAt(0).isVisible = true
    }

    private fun initListeners() = with(binding) {
        ivLetterReplyChevron.setOnClickListener {
            tvLetterReplyReceivedContent.isVisible = !tvLetterReplyReceivedContent.isVisible
            val chevron = if(tvLetterReplyReceivedContent.isVisible) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
            ivLetterReplyChevron.setImageResource(chevron)
        }
        premiumLetterColorList.forEach { letterColor ->
            letterColor.setOnClickListener { clickedView ->
                val letterColor = when(clickedView.id) {
                    R.id.cv_letter_reply_color_pink -> LetterBackgroundColor.PINK
                    R.id.cv_letter_reply_color_green -> LetterBackgroundColor.GREEN
                    R.id.cv_letter_reply_color_blue -> LetterBackgroundColor.BLUE
                    else -> LetterBackgroundColor.PURPLE
                }
                val dialog = PremiumLetterBuyDialog(letterColor = letterColor).apply { isCancelable = false }
                dialog.show(childFragmentManager, PremiumLetterBuyDialog.TAG)
                applyScreenBlur(BlurLevel.BASE)
            }
        }
        etLetterReplyContent.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) = Unit
            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                btnLetterReply.isEnabled = !text.isNullOrBlank()
                tvLetterReplyContentLength.text = "${text?.length}/$MAX_LENGTH"
            }
        })
        ivLetterReplyTooltip.setOnClickListener {
            showTooltipPopup(anchorView = ivLetterReplyTooltip)
        }
    }

    private fun showTooltipPopup(anchorView: View) {
        val popupBinding = LayoutLetterTooltipPopupBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        popupBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupBinding.root.measuredWidth
        popupWindow.showAsDropDown(
            anchorView,
            0 - popupWidth + anchorView.width,
            0
        )
    }

    companion object {
        private const val MAX_LENGTH = 360
    }
}