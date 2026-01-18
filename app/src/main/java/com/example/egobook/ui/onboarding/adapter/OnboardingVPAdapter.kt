package com.example.egobook.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egobook.ui.onboarding.view.OnboardingFirstFragment
import com.example.egobook.ui.onboarding.view.OnboardingSecondFragment
import com.example.egobook.ui.onboarding.view.OnboardingThirdFragment
import com.example.egobook.ui.onboarding.view.OnboardingFourthFragment
import com.example.egobook.ui.onboarding.view.OnboardingFifthFragment

class OnboardingVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
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