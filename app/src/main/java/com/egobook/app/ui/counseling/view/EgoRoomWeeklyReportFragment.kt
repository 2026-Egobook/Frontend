package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomWeeklyReportBinding
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.ui.counseling.adapter.CounselingWeeklyReportAdapter
import com.egobook.app.ui.counseling.model.DailyAndWeeklyNotificationModel
import com.egobook.app.ui.counseling.viewmodel.WeeklyReportViewModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EgoRoomWeeklyReportFragment : Fragment(R.layout.fragment_ego_room_weekly_report) {
    private lateinit var binding: FragmentEgoRoomWeeklyReportBinding
    private val viewModel: WeeklyReportViewModel by viewModels()
    private val counselingWeeklyReportAdapter = CounselingWeeklyReportAdapter { item ->
        if(item.isLocked) {
            val dialog = WeeklyReportUnlockDialog(startDate = item.startDate)
            dialog.show(childFragmentManager, WeeklyReportUnlockDialog.TAG)
        } else {
            val action = EgoRoomFragmentDirections.actionMenuEgoRoomToCounselingWeeklyReportDetailFragment(startDate = item.startDate)
            findNavController().navigate(action)
        }
    }
    private var isNotificationEnabled: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomWeeklyReportBinding.bind(view)
        initViews()
        initListeners()
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

    private fun initListeners() = with(binding) {
        cvCounselingWeeklyReportStyleSharp.setOnClickListener {
            viewModel.updateWeeklyReportStyle(reportStyle = ReportStyle.SHARP)
        }
        cvCounselingWeeklyReportStyleSoft.setOnClickListener {
            viewModel.updateWeeklyReportStyle(reportStyle = ReportStyle.SOFT)
        }
        cvCounselingWeeklyReportStyleObjective.setOnClickListener {
            viewModel.updateWeeklyReportStyle(reportStyle = ReportStyle.OBJECTIVE)
        }
        ivCounselingWeeklyReportNotification.setOnClickListener {
            if (isNotificationEnabled == true) {
                viewModel.updateWeeklyReportNotification(isEnabled = false)
            } else {
                viewModel.updateWeeklyReportNotification(isEnabled = true)
            }
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weeklyReportList.collectLatest { pagingData ->
                        counselingWeeklyReportAdapter.submitData(lifecycle, pagingData)
                    }
                }
                launch {
                    counselingWeeklyReportAdapter.loadStateFlow.collect { loadStates ->
                        val isListEmpty = loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && counselingWeeklyReportAdapter.itemCount == 0
                        recyclerviewCounselingWeeklyReport.isVisible = !isListEmpty
                        llCounselingWeeklyReportPlaceholder.isVisible = isListEmpty
                    }
                }
                launch {
                    viewModel.dailyAndWeeklyNotification.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<DailyAndWeeklyNotificationModel> -> {
                                val notificationStatus = state.data
                                updateNotificationUi(notificationStatus.isWeeklyAnalysisEnabled)
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateNotificationStatus.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Boolean> -> {
                                val isEnabled = state.data
                                updateNotificationUi(isEnabled)
                                val toastMessage =
                                    if (isEnabled) R.string.notification_on else R.string.notification_off
                                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.weeklyReportStyle.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<ReportStyle> -> {
                                val reportStyle = state.data
                                updateReportStyleUi(reportStyle = reportStyle)
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateReportStyleResult.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<ReportStyle> -> {
                                val reportStyle = state.data
                                updateReportStyleUi(reportStyle = reportStyle)
                                Toast.makeText(context, "분위기가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateNotificationUi(isEnabled: Boolean) = with(binding) {
        this@EgoRoomWeeklyReportFragment.isNotificationEnabled = isEnabled
        if (isEnabled) {
            tvCounselingWeeklyReportNotification.text =
                getString(R.string.counseling_weekly_report_notification_on)
            ivCounselingWeeklyReportNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_notification_on
                )
            )
        } else {
            tvCounselingWeeklyReportNotification.text =
                getString(R.string.counseling_weekly_report_notification_off)
            ivCounselingWeeklyReportNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_notification_off
                )
            )
        }
    }

    private fun updateReportStyleUi(reportStyle: ReportStyle) = with(binding) {
        cvCounselingWeeklyReportStyleSharp.isSelected = (reportStyle == ReportStyle.SHARP)
        cvCounselingWeeklyReportStyleSoft.isSelected = (reportStyle == ReportStyle.SOFT)
        cvCounselingWeeklyReportStyleObjective.isSelected = (reportStyle == ReportStyle.OBJECTIVE)
    }

    private fun fetchData() {
        viewModel.getDailyAndWeeklyNotification()
        viewModel.fetchWeeklyReport(size = 10)
        viewModel.fetchWeeklyReportStyle()
    }
}