package com.egobook.app.ui.square.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentLetterWriteBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LetterWriteFragment : Fragment(R.layout.fragment_letter_write) {
    private lateinit var binding: FragmentLetterWriteBinding
    private val letterColorList by lazy {
        listOf(binding.cvLetterColorBeige, binding.cvLetterColorPink, binding.cvLetterColorLeaf, binding.cvLetterColorMint, binding.cvLetterColorLavender)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterWriteBinding.bind(view)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        ivLetterWriteBack.setOnClickListener { findNavController().popBackStack() }
        letterColorList.forEach { letterColor ->
            letterColor.setOnClickListener { clickedView ->
                letterColorList.forEach { card ->
                    card.isSelected = false
                    card.getChildAt(0).isVisible = false
                }
                clickedView.isSelected = true
                (clickedView as MaterialCardView).getChildAt(0).isVisible = true
                when(clickedView.id) {
                    R.id.cv_letter_color_beige -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_beige, null)}
                    R.id.cv_letter_color_pink -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_pink, null)}
                    R.id.cv_letter_color_leaf -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_leaf, null)}
                    R.id.cv_letter_color_mint -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_mint, null)}
                    R.id.cv_letter_color_lavender -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_lavender, null)}
                }
            }
        }
        etLetterWriteContent.addTextChangedListener(object: TextWatcher {
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
                btnLetterSendAnonymous.isEnabled = !text.isNullOrBlank()
                btnLetterSendFriend.isEnabled = !text.isNullOrBlank()
                tvLetterWriteContentLength.text = "${text?.length}/$MAX_LENGTH"
            }
        })
    }

    companion object {
        private const val MAX_LENGTH = 360
    }
}