package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingWeeklyReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CounselingWeeklyReportFragment : Fragment(R.layout.fragment_counseling_weekly_report) {
    private lateinit var binding: FragmentCounselingWeeklyReportBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportBinding.bind(view)
    }
}