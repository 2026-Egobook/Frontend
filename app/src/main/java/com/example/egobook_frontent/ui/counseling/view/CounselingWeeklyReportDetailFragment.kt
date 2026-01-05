package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import android.view.View
import com.example.egobook_frontent.R
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.databinding.FragmentCounselingWeeklyReportDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CounselingWeeklyReportDetailFragment : Fragment(R.layout.fragment_counseling_weekly_report_detail) {
    private lateinit var binding: FragmentCounselingWeeklyReportDetailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportDetailBinding.bind(view)
    }
}