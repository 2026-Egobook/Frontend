package com.example.egobook.ui.square

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook.R
import com.example.egobook.databinding.FragmentFriendsListBinding

class FriendsListFragment : Fragment(R.layout.fragment_friends_list) {
    private lateinit var binding: FragmentFriendsListBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsListBinding.bind(view)
    }
}