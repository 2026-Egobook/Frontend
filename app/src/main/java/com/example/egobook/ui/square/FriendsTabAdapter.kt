package com.example.egobook.ui.square

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendsTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FriendsListFragment()
            else -> FriendsPendingListFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}