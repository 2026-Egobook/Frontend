package com.example.egobook_frontent.ui.onboarding.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.egobook_frontent.databinding.FragmentOnboardingContainerBinding
import com.example.egobook_frontent.ui.onboarding.adapter.OnboardingVPAdapter
import me.relex.circleindicator.CircleIndicator3

class OnboardingContainerFragment : Fragment() {

    private var _binding: FragmentOnboardingContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
    }

    private fun setAdapter() {
        val onboardingVPAdapter = OnboardingVPAdapter(this)
        binding.vpOnboarding.adapter = onboardingVPAdapter

        binding.circleIndicator.setViewPager(binding.vpOnboarding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 필수
    }
}