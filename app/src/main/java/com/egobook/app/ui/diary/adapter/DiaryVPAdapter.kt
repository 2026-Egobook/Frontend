package com.egobook.app.ui.diary.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.egobook.app.ui.diary.view.DiaryListFragment

class DiaryVPAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return DiaryListFragment.newInstance(position)
    }
}