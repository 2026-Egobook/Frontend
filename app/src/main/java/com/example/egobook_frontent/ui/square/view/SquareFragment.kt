package com.example.egobook_frontent.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentSquareBinding

class SquareFragment : Fragment(R.layout.fragment_square) {
    private lateinit var binding: FragmentSquareBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareBinding.bind(view)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnSquareNavigateToFriends.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_friendsFragment)
        }
        tvSquareTodayQuestionMyRepliesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_myRepliesHistoryFragment)
        }
        tvSquareAllUsersRepliesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_squareAllRepliesFragment)
        }
        tvSquareMyLettersViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_myLettersFragment)
        }
    }
}
