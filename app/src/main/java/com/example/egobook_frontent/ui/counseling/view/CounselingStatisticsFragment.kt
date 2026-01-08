package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CounselingStatisticsFragment : Fragment(R.layout.fragment_counseling_statistics) {
    private lateinit var binding: FragmentCounselingStatisticsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingStatisticsBinding.bind(view)
    }
}