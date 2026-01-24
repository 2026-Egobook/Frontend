package com.example.egobook_frontent.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentLetterReplyBinding

class LetterReplyFragment : Fragment(R.layout.fragment_letter_reply) {
    private lateinit var binding: FragmentLetterReplyBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterReplyBinding.bind(view)
    }

}