package com.example.egobook_frontent.ui.square.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egobook_frontent.ui.square.view.FriendsListFragment
import com.example.egobook_frontent.ui.square.view.FriendsPendingListFragment

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