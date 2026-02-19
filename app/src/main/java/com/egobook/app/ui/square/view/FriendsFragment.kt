package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentSquareFriendsBinding
import com.egobook.app.ui.square.adapter.FriendsTabAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FriendsFragment : Fragment(R.layout.fragment_square_friends) {
    private lateinit var binding: FragmentSquareFriendsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareFriendsBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        vpSquareFriends.adapter = FriendsTabAdapter(this@FriendsFragment)
        TabLayoutMediator(tlSquareFriends, vpSquareFriends) { tab, position ->
            tab.text = when (position) {
                0 -> "친구목록"
                else -> "승인대기"
            }
        }.attach()
    }

    private fun initListeners() = with(binding) {
        ivSquareFriendsBack.setOnClickListener {
            findNavController().popBackStack()
        }
        btnSquareFriendsAdd.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = FriendAddDialog().apply {
                isCancelable = false
            }
            dialog.show(childFragmentManager, FriendAddDialog.TAG)
        }
    }
}