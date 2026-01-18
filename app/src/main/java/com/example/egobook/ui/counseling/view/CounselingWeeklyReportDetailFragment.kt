package com.example.egobook.ui.counseling.view

import android.os.Bundle
import android.view.View
import com.example.egobook.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.egobook.databinding.FragmentCounselingWeeklyReportDetailBinding
import com.example.egobook.ui.counseling.model.WeeklyReportModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CounselingWeeklyReportDetailFragment : Fragment(R.layout.fragment_counseling_weekly_report_detail) {
    private lateinit var binding: FragmentCounselingWeeklyReportDetailBinding
    private val args: CounselingWeeklyReportDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportDetailBinding.bind(view)
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
