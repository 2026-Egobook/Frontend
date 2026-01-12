package com.example.egobook_frontent.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egobook_frontent.ui.onboarding.view.OnboardingFragment1
import com.example.egobook_frontent.ui.onboarding.view.OnboardingFragment2
import com.example.egobook_frontent.ui.onboarding.view.OnboardingFragment3
import com.example.egobook_frontent.ui.onboarding.view.OnboardingFragment4
import com.example.egobook_frontent.ui.onboarding.view.OnboardingFragment5

class OnboardingVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment1()
            1 -> OnboardingFragment2()
            2 -> OnboardingFragment3()
            3 -> OnboardingFragment4()
            4 -> OnboardingFragment5()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int  = 5

}