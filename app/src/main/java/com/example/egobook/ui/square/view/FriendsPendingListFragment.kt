package com.example.egobook.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook.R
import com.example.egobook.databinding.FragmentFriendsPendingListBinding

class FriendsPendingListFragment : Fragment(R.layout.fragment_friends_pending_list) {
    private lateinit var binding: FragmentFriendsPendingListBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsPendingListBinding.bind(view)
    }
}