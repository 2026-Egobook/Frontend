package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.egobook.app.R
import com.egobook.app.databinding.FragmentLetterReplyBinding

class LetterReplyFragment : Fragment(R.layout.fragment_letter_reply) {
    private lateinit var binding: FragmentLetterReplyBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterReplyBinding.bind(view)
    }

}