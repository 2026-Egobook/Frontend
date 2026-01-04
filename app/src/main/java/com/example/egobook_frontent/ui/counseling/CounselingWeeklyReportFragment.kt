package com.example.egobook_frontent.ui.counseling

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingWeeklyReportBinding

private lateinit var binding: FragmentCounselingWeeklyReportBinding
class CounselingWeeklyReportFragment : Fragment(R.layout.fragment_counseling_weekly_report) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingWeeklyReportBinding.bind(view)
    }
}