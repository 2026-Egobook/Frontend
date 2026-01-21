package com.example.egobook.ui.square

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook.R
import com.example.egobook.databinding.FragmentFriendsListBinding

class FriendsListFragment : Fragment(R.layout.fragment_friends_list) {
    private lateinit var binding: FragmentFriendsListBinding
    private val adapter = FriendsListAdapter()
    private val dummyList = listOf(
        FriendModel(1, R.drawable.default_turtle, 1, "친구1"),
        FriendModel(2, R.drawable.default_turtle, 2, "친구2"),
        FriendModel(3, R.drawable.default_turtle, 3, "친구3")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsListBinding.bind(view)
        initViews()
    }

    private fun initViews() = with(binding) {
        rvFriendsList.adapter = adapter
        adapter.submitList(dummyList)
    }
}