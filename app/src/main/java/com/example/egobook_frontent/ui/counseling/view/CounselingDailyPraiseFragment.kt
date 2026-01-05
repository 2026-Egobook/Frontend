package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingDailyPraiseBinding
import com.example.egobook_frontent.ui.counseling.viewmodel.DailyPraiseViewModel
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CounselingDailyPraiseFragment : Fragment(R.layout.fragment_counseling_daily_praise) {

    private lateinit var binding: FragmentCounselingDailyPraiseBinding
    private val viewModel: DailyPraiseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingDailyPraiseBinding.bind(view)
        initViews()
        initObservers()
    }

    private fun initViews() = with(binding) {
        viewModel.fetchDailyPraise()
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyPraise.collect { state ->
                    when(state) {
                        UiState.Loading -> { /* 프로그래스바 표시 */ }
                        is UiState.Success -> {
                            val messageList = state.data
                            // TODO: 리사클러뷰 전달
                        }
                        is UiState.Failure -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}