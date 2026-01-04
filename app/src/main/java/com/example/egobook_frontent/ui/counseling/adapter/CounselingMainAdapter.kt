package com.example.egobook_frontent.ui.counseling.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egobook_frontent.ui.counseling.CounselingDailyPraiseFragment
import com.example.egobook_frontent.ui.counseling.CounselingStatisticsFragment
import com.example.egobook_frontent.ui.counseling.CounselingWeeklyReportFragment

class CounselingMainAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> CounselingDailyPraiseFragment()
            1 -> CounselingWeeklyReportFragment()
            else -> CounselingStatisticsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}