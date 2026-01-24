package com.example.egobook_frontent.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentLetterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LetterFragment : Fragment(R.layout.fragment_letter) {
    private lateinit var binding: FragmentLetterBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterBinding.bind(view)
    }
}