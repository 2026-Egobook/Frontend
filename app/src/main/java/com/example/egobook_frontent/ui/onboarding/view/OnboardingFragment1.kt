package com.example.egobook_frontent.ui.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment() {

    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 필수
    }
}