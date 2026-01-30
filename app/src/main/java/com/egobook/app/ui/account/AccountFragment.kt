package com.egobook.app.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.egobook.app.R
import androidx.navigation.fragment.findNavController
import com.egobook.app.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val blurRadius = 5f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun setupBlur() {
        binding.blurView.setupWith(binding.blurTarget)
            .setBlurRadius(blurRadius)
            .setBlurAutoUpdate(true)
    }
    fun clearBlur() {
        binding.blurView.visibility = View.GONE
    }


    private fun setClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
            }

            btnIntegrate.setOnClickListener {
                binding.blurView.visibility = View.VISIBLE
                AccountBottomSheetFragment()
                    .show(childFragmentManager, AccountBottomSheetFragment.TAG)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
