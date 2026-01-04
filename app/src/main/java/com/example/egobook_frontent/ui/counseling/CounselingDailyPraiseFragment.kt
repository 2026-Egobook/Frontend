package com.example.egobook_frontent.ui.counseling

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingDailyPraiseBinding

private lateinit var binding: FragmentCounselingDailyPraiseBinding
class CounselingDailyPraiseFragment : Fragment(R.layout.fragment_counseling_daily_praise) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingDailyPraiseBinding.bind(view)
    }
}