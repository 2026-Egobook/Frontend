package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomWeeklyReportDetailBinding
import com.egobook.app.ui.counseling.model.WeeklyReportModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EgoRoomWeeklyReportDetailFragment : Fragment(R.layout.fragment_ego_room_weekly_report_detail) {
    private lateinit var binding: FragmentEgoRoomWeeklyReportDetailBinding
    private val args: EgoRoomWeeklyReportDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomWeeklyReportDetailBinding.bind(view)
        val weeklyReportItem: WeeklyReportModel = args.weeklyReportItem
        initViews(weeklyReportItem)
        initListeners()
    }

    private fun initViews(item: WeeklyReportModel) = with(binding) {
        tvCounselingWeeklyReportDetailDatetime.text = item.date
        tvCounselingWeeklyReportDetailAnalysis.text = item.content.analysis
        tvCounselingWeeklyReportDetailPraisePoint.text = item.content.praisePoint
        tvCounselingWeeklyReportDetailImprovement.text = item.content.improvement
        tvCounselingWeeklyReportDetailManagement.text = item.content.management
        tvCounselingWeeklyReportDetailEncouragement.text = item.content.encouragement
    }
    private fun initListeners() = with(binding) {
        ivCounselingWeeklyReportDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
