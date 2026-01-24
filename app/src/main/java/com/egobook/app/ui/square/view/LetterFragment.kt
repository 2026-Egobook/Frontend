package com.egobook.app.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.egobook.app.R
import com.egobook.app.databinding.FragmentLetterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LetterFragment : Fragment(R.layout.fragment_letter) {
    private lateinit var binding: FragmentLetterBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterBinding.bind(view)
    }
}