package com.egobook.app.ui.account.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentAccountBinding
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()
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
        setupBlur()
        observeUserIdState()
    }

    private fun observeUserIdState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userIdState.collect { state ->
                    when (state) {

                        is UiState.Idle -> Unit

                        is UiState.Loading -> {
                        }

                        is UiState.Success -> {
                            binding.tvRealAccountId.text = state.data
                        }

                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), "유저 id를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
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


    //=============다이알로그 출력용 블러뷰 세팅====================

    private fun setupBlur() {
        binding.blurView.setupWith(binding.blurTarget)
            .setBlurRadius(blurRadius)
            .setBlurAutoUpdate(true)
    }
    fun clearBlur() {
        binding.blurView.visibility = View.GONE
    }

}