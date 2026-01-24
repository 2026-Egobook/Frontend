package com.egobook.app.ui.counseling.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.egobook.app.ui.counseling.view.EgoRoomDailyPraiseFragment
import com.egobook.app.ui.counseling.view.EgoRoomStatisticsFragment
import com.egobook.app.ui.counseling.view.EgoRoomWeeklyReportFragment

class CounselingMainAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> EgoRoomDailyPraiseFragment()
            1 -> EgoRoomWeeklyReportFragment()
            else -> EgoRoomStatisticsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}