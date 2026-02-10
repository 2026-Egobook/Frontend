package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomWeeklyReportDetailBinding
import com.egobook.app.ui.counseling.model.WeeklyReportDetailModel
import com.egobook.app.ui.counseling.viewmodel.WeeklyReportViewModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EgoRoomWeeklyReportDetailFragment : Fragment(R.layout.fragment_ego_room_weekly_report_detail) {
    private lateinit var binding: FragmentEgoRoomWeeklyReportDetailBinding
    private val startDate: String by lazy {
        val args: EgoRoomWeeklyReportDetailFragmentArgs by navArgs()
        args.startDate
    }
    private val viewModel: WeeklyReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomWeeklyReportDetailBinding.bind(view)
        fetchData()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getWeeklyReportByDate(startDate = startDate)
    }

    private fun initListeners() = with(binding) {
        ivCounselingWeeklyReportDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyReportByDate.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<WeeklyReportDetailModel> -> {
                            val reportDetailItem = state.data
                            tvCounselingWeeklyReportDetailDatetime.text = "${reportDetailItem.startDate} ~ ${reportDetailItem.endDate}"
                            tvCounselingWeeklyReportDetailSummary.text = reportDetailItem.summary
                            tvCounselingWeeklyReportDetailPraisePoint.text = reportDetailItem.praisePoints
                            tvCounselingWeeklyReportDetailImprovement.text = reportDetailItem.improvementPoints
                            tvCounselingWeeklyReportDetailManagement.text = reportDetailItem.managementAdvice
                            tvCounselingWeeklyReportDetailEncouragement.text = reportDetailItem.supportMessage
                        }
                    }
                }
            }
        }
    }
}
