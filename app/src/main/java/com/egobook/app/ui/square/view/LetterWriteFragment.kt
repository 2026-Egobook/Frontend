package com.egobook.app.ui.square.view

import android.os.Bundle
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
    }
}