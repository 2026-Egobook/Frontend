package com.example.egobook.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.egobook.R
import com.example.egobook.databinding.FragmentMyRepliesHistoryBinding
import com.example.egobook.ui.square.adapter.MyRepliesHistoryAdapter
import com.example.egobook.ui.square.model.ReplyModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRepliesHistoryFragment : Fragment(R.layout.fragment_my_replies_history) {
    private lateinit var binding: FragmentMyRepliesHistoryBinding
    private val dummyList = listOf(
        ReplyModel(
            id = 1,
            date = "2025.12.25",
            question = "올해 크리스마스에 당신이 가장 행복했던 순간은 언제인가요?",
            answer = "오랜만에 가족들과 모여서 맛있는 저녁을 먹고, 따뜻한 차를 마시며 도란도란 이야기 나눴던 시간이 가장 행복했어요. 소박하지만 확실한 행복이었죠."
        ),
        ReplyModel(
            id = 2,
            date = "2025.12.26",
            question = "낯선 이에게서 온 편지 중 가장 기억에 남는 문장이 있나요?",
            answer = "'당신은 생각보다 더 단단한 사람이에요'라는 말이요. 힘들었던 시기에 정말 큰 위로가 되었습니다."
        ),
        ReplyModel(
            id = 3,
            date = "2025.12.27",
            question = "오늘 하루를 한 단어로 정의한다면?",
            answer = "윤슬. 잔잔한 호수 위에 비친 햇살처럼 평화롭고 반짝이는 하루였거든요."
        ),
        ReplyModel(
            id = 4,
            date = "2025.12.28",
            question = "가끔은 도망치고 싶을 때, 당신만의 도피처는 어디인가요? 아주 구체적으로 설명해주세요.",
            answer = "저는 이어폰을 끼고 동네 한 바퀴를 크게 돌아요. 좋아하는 음악에만 집중하다 보면 머릿속이 비워지고 다시 시작할 힘이 생기더라고요. 특히 해 질 녘 공원의 벤치를 좋아합니다."
        )
    )
    private val adapter = MyRepliesHistoryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyRepliesHistoryBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        rvMyRepliesHistory.adapter = adapter
        adapter.submitList(dummyList)
    }
    private fun initListeners() = with(binding) {
        ivMyRepliesHistoryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}