package com.example.egobook.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook.R
import com.example.egobook.databinding.FragmentEgoRoomBinding
import com.example.egobook.ui.counseling.adapter.CounselingMainAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EgoRoomFragment : Fragment(R.layout.fragment_ego_room) {
    private lateinit var binding: FragmentEgoRoomBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomBinding.bind(view)
        initViews()
    }

    private fun initViews() = with(binding) {
        vpCounseling.adapter = CounselingMainAdapter(this@EgoRoomFragment)
        TabLayoutMediator(tlCounseling, vpCounseling) { tab, position ->
            tab.text = when (position) {
                0 -> "일간 칭찬서"
                1 -> "주간 리포트"
                else -> "통계"
            }
        }.attach()
    }
}