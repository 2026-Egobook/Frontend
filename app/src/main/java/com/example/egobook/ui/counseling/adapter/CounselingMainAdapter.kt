package com.example.egobook.ui.counseling.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egobook.ui.counseling.view.CounselingDailyPraiseFragment
import com.example.egobook.ui.counseling.view.CounselingStatisticsFragment
import com.example.egobook.ui.counseling.view.CounselingWeeklyReportFragment

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