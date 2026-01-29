package com.egobook.app.ui.onboarding.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egobook.app.databinding.FragmentOnboardingFifthBinding

class OnboardingFifthFragment : Fragment() {

    private var _binding: FragmentOnboardingFifthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingFifthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartEgobook.setOnClickListener {
            // OnboardingActivity의 메서드 호출
            (activity as? OnboardingActivity)?.navigateToMain()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
