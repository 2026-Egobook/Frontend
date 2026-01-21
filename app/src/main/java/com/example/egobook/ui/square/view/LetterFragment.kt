package com.example.egobook.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook.R
import com.example.egobook.databinding.FragmentLetterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LetterFragment : Fragment(R.layout.fragment_letter) {
    private lateinit var binding: FragmentLetterBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterBinding.bind(view)
    }
}