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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingWeeklyReportBinding
import com.example.egobook_frontent.domain.model.NotificationType
import com.example.egobook_frontent.domain.model.ReportStyle
import com.example.egobook_frontent.ui.counseling.adapter.CounselingWeeklyReportAdapter
import com.example.egobook_frontent.ui.counseling.model.WeeklyReportStyleModel
import com.example.egobook_frontent.ui.counseling.viewmodel.WeeklyReportViewModel
import com.example.egobook_frontent.ui.notification.model.NotificationModel
import com.example.egobook_frontent.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CounselingWeeklyReportFragment : Fragment(R.layout.fragment_counseling_weekly_report) {
    private lateinit var binding: FragmentCounselingWeeklyReportBinding
    private val viewModel: WeeklyReportViewModel by viewModels()
    private val counselingWeeklyReportAdapter = CounselingWeeklyReportAdapter { item ->
        val action = CounselingMainFragmentDirections.actionMenuSquareToCounselingWeeklyReportDetailFragment(weeklyReportItem = item)
        findNavController().navigate(action)
    }
    private var isNotificationEnabled: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportBinding.bind(view)
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
                viewModel.updateNotificationStatus(
                    type = NotificationType.WEEKLY_REPORT,
                    isEnabled = false
                )
            } else {
                viewModel.updateNotificationStatus(
                    type = NotificationType.WEEKLY_REPORT,
                    isEnabled = true
                )
            }
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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
                launch {
                    viewModel.notificationStatus.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<NotificationModel> -> {
                                val notificationStatus = state.data
                                updateNotificationUi(notificationStatus.isWeeklyReportEnabled)
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateNotificationResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Boolean> -> {
                                val isEnabled = state.data
                                updateNotificationUi(isEnabled)
                                val toastMessage = if(isEnabled) R.string.notification_on else R.string.notification_off
                                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.weeklyReportStyle.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<WeeklyReportStyleModel> -> {
                                val reportStyle = state.data.type
                                updateReportStyleUi(reportStyle = reportStyle)
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateReportStyleResult.collect { state ->
                        when(state) {
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
        this@CounselingWeeklyReportFragment.isNotificationEnabled = isEnabled
        if(isEnabled) {
            tvCounselingWeeklyReportNotification.text = getString(R.string.counseling_weekly_report_notification_on)
            ivCounselingWeeklyReportNotification.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_on))
        } else {
            tvCounselingWeeklyReportNotification.text = getString(R.string.counseling_weekly_report_notification_off)
            ivCounselingWeeklyReportNotification.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_off))
        }
    }

    private fun updateReportStyleUi(reportStyle: ReportStyle) = with(binding) {
        cvCounselingWeeklyReportStyleSharp.isSelected = (reportStyle == ReportStyle.SHARP)
        cvCounselingWeeklyReportStyleSoft.isSelected = (reportStyle == ReportStyle.SOFT)
        cvCounselingWeeklyReportStyleObjective.isSelected = (reportStyle == ReportStyle.OBJECTIVE)
    }

    private fun fetchData() {
        viewModel.fetchWeeklyReport()
        viewModel.fetchNotificationStatus()
        viewModel.fetchWeeklyReportStyle()
    }
}