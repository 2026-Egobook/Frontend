package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingStatisticsBinding

private lateinit var binding: FragmentCounselingStatisticsBinding
class CounselingStatisticsFragment : Fragment(R.layout.fragment_counseling_statistics) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingStatisticsBinding.bind(view)
    }
}