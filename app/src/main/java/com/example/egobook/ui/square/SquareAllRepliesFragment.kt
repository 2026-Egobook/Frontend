package com.example.egobook.ui.square

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook.R
import com.example.egobook.databinding.FragmentSquareAllRepliesBinding

class SquareAllRepliesFragment : Fragment(R.layout.fragment_square_all_replies) {
    private lateinit var binding: FragmentSquareAllRepliesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareAllRepliesBinding.bind(view)
    }

}