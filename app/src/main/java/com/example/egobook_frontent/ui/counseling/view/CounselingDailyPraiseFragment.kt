package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingDailyPraiseBinding
import com.example.egobook_frontent.ui.counseling.adapter.CounselingDailyPraiseAdapter
import com.example.egobook_frontent.ui.counseling.viewmodel.DailyPraiseViewModel
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CounselingDailyPraiseFragment : Fragment(R.layout.fragment_counseling_daily_praise) {

    private lateinit var binding: FragmentCounselingDailyPraiseBinding
    private val viewModel: DailyPraiseViewModel by viewModels()
    private val counselingDailyPraiseAdapter = CounselingDailyPraiseAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingDailyPraiseBinding.bind(view)
        initViews()
        initObservers()
        fetchData()
    }

    private fun initViews() = with(binding) {
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_praise)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        with(recyclerviewCounselingDailyPraise) {
            adapter = counselingDailyPraiseAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyPraise.collect { state ->
                    when(state) {
                        UiState.Loading -> { /* 프로그래스바 표시 */ }
                        is UiState.Success -> {
                            val messageList = state.data
                            counselingDailyPraiseAdapter.submitList(messageList)
                            recyclerviewCounselingDailyPraise.isVisible = !messageList.isEmpty()
                            llCounselingDailyPraisePlaceholder.isVisible = messageList.isEmpty()
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

    private fun fetchData() {
        viewModel.fetchDailyPraise()
    }
}