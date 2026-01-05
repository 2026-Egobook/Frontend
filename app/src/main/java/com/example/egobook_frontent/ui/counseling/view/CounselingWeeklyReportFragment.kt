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
import com.example.egobook_frontent.databinding.FragmentCounselingWeeklyReportBinding
import com.example.egobook_frontent.ui.counseling.adapter.CounselingWeeklyReportAdapter
import com.example.egobook_frontent.ui.counseling.viewmodel.WeeklyReportViewModel
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CounselingWeeklyReportFragment : Fragment(R.layout.fragment_counseling_weekly_report) {
    private lateinit var binding: FragmentCounselingWeeklyReportBinding
    private val viewModel: WeeklyReportViewModel by viewModels()
    private val counselingWeeklyReportAdapter = CounselingWeeklyReportAdapter { item ->
        // TODO: navigation 으로 화면 이동 + Safe Args 전달
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportBinding.bind(view)
        initViews()
        initObservers()
        fetchData()
    }

    private fun initViews() = with(binding) {
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_praise)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        with(recyclerviewCounselingWeeklyReport) {
            adapter = counselingWeeklyReportAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyReportList.collect { state ->
                    when(state) {
                        UiState.Loading -> {}
                        is UiState.Success -> {
                            val weeklyReportList = state.data
                            counselingWeeklyReportAdapter.submitList(weeklyReportList)
                            recyclerviewCounselingWeeklyReport.isVisible = !weeklyReportList.isEmpty()
                            llCounselingWeeklyReportPlaceholder.isVisible = weeklyReportList.isEmpty()
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
        viewModel.fetchWeeklyReport()
    }
}