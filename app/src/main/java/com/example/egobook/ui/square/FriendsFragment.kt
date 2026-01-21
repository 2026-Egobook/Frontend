package com.example.egobook.ui.square

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook.R
import com.example.egobook.databinding.FragmentSquareFriendsBinding

class FriendsFragment : Fragment(R.layout.fragment_square_friends) {
    private lateinit var binding: FragmentSquareFriendsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareFriendsBinding.bind(view)
    }
}