package com.egobook.app.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.egobook.app.ui.onboarding.view.OnboardingFirstFragment
import com.egobook.app.ui.onboarding.view.OnboardingSecondFragment
import com.egobook.app.ui.onboarding.view.OnboardingThirdFragment
import com.egobook.app.ui.onboarding.view.OnboardingFourthFragment
import com.egobook.app.ui.onboarding.view.OnboardingFifthFragment

class OnboardingVPAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFirstFragment()
            1 -> OnboardingSecondFragment()
            2 -> OnboardingThirdFragment()
            3 -> OnboardingFourthFragment()
            4 -> OnboardingFifthFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int  = 5

}