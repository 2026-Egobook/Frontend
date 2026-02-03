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
import com.egobook.app.R
import com.egobook.app.databinding.FragmentLetterReplyBinding
import com.egobook.app.databinding.LayoutLetterTooltipPopupBinding
import com.egobook.app.ui.square.model.letter.LetterBackgroundColor
import com.google.android.material.card.MaterialCardView

class LetterReplyFragment : Fragment(R.layout.fragment_letter_reply) {
    private lateinit var binding: FragmentLetterReplyBinding
    private val letterItem by lazy {
        val args: LetterReplyFragmentArgs by navArgs()
        args.letterItem
    }
    private val letterColorList by lazy {
        listOf(binding.cvLetterReplyColorBeige, binding.cvLetterReplyColorPink, binding.cvLetterReplyColorLeaf, binding.cvLetterReplyColorMint, binding.cvLetterReplyColorLavender)
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
        letterColorList.forEach { letterColor ->
            letterColor.setOnClickListener { clickedView ->
                letterColorList.forEach { card ->
                    card.isSelected = false
                    card.getChildAt(0).isVisible = false
                }
                clickedView.isSelected = true
                (clickedView as MaterialCardView).getChildAt(0).isVisible = true
                when(clickedView.id) {
                    R.id.cv_letter_reply_color_beige -> {
                        cvLetterReplyContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_beige, null)
                        this@LetterReplyFragment.letterColor = LetterBackgroundColor.BEIGE
                    }
                    R.id.cv_letter_reply_color_pink -> {
                        cvLetterReplyContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_pink, null)
                        this@LetterReplyFragment.letterColor = LetterBackgroundColor.PINK
                    }
                    R.id.cv_letter_reply_color_leaf -> {
                        cvLetterReplyContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_leaf, null)
                        this@LetterReplyFragment.letterColor = LetterBackgroundColor.LEAF
                    }
                    R.id.cv_letter_reply_color_mint -> {
                        cvLetterReplyContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_mint, null)
                        this@LetterReplyFragment.letterColor = LetterBackgroundColor.MINT
                    }
                    R.id.cv_letter_reply_color_lavender -> {
                        cvLetterReplyContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_lavender, null)
                        this@LetterReplyFragment.letterColor = LetterBackgroundColor.LAVENDER
                    }
                }
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