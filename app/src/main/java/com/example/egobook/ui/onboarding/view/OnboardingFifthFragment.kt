package com.example.egobook.ui.onboarding.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.egobook.R
import com.example.egobook.databinding.FragmentOnboardingFifthBinding

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
            findNavController().navigate(R.id.action_onboarding_to_home)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
