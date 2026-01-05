package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingMainBinding
import com.example.egobook_frontent.ui.counseling.adapter.CounselingMainAdapter
import com.google.android.material.tabs.TabLayoutMediator

class CounselingMainFragment : Fragment(R.layout.fragment_counseling_main) {
    private lateinit var binding: FragmentCounselingMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingMainBinding.bind(view)
        initViews()
    }

    private fun initViews() = with(binding) {
        vpCounseling.adapter = CounselingMainAdapter(this@CounselingMainFragment)
        TabLayoutMediator(tlCounseling, vpCounseling) { tab, position ->
            tab.text = when (position) {
                0 -> "일간 칭찬서"
                1 -> "주간 리포트"
                else -> "통계"
            }
        }.attach()
    }
}