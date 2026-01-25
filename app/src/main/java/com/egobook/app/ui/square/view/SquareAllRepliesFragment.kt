package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentSquareAllRepliesBinding
import com.egobook.app.ui.square.adapter.SquareAllRepliesAdapter
import com.egobook.app.ui.square.model.friend.ReplyModel

class SquareAllRepliesFragment : Fragment(R.layout.fragment_square_all_replies) {
    private lateinit var binding: FragmentSquareAllRepliesBinding
    private val adapter = SquareAllRepliesAdapter()
    private val dummyData = listOf(
        ReplyModel(
            id = 1,
            image = R.drawable.img_temp_square_user_thumbnail,
            level = 1,date = "2025.12.20",
            question = "Q. 다시 태어난다면 어느 나라에서 태어나고 싶으신가요?",
            answer = "저는 아이슬란드에서 태어나보고 싶어요. 끝없이 펼쳐진 오로라를 매일 밤 마당에서 구경하며, 자연의 경이로움 속에서 고요하게 나 자신에게 집중하는 삶을 살고 싶거든요."
        ),
        ReplyModel(
            id = 2,
            image = R.drawable.img_temp_square_user_thumbnail,
            level = 2,
            date = "2025.12.21",
            question = "Q. 다시 태어난다면 어느 나라에서 태어나고 싶으신가요?",
            answer = "일본의 작고 조용한 시골 마을이요. 아침마다 자전거를 타고 동네를 한 바퀴 돌고, 오후에는 동네 카페에서 책을 읽으며 소박하고 느릿한 일상을 즐기고 싶습니다."
        ),
        ReplyModel(
            id = 3,
            image = R.drawable.img_temp_square_user_thumbnail,
            level = 3,
            date = "2025.12.22",
            question = "Q. 다시 태어난다면 어느 나라에서 태어나고 싶으신가요?",
            answer = "스위스의 알프스 산맥 아래 마을에서 태어나고 싶습니다. 매일 아침 창문을 열면 보이는 만년설과 푸른 들판을 보며 평화로운 마음으로 하루를 시작하는 기분은 어떨지 항상 궁금해요."
        ),
        ReplyModel(
            id = 4,
            image = R.drawable.img_temp_square_user_thumbnail,
            level = 4,
            date = "2025.12.23",
            question = "Q. 다시 태어난다면 어느 나라에서 태어나고 싶으신가요?",
            answer = "프랑스 파리요. 예술과 낭만이 가득한 거리에서 매일 새로운 영감을 얻고, 해 질 녘 에펠탑을 보며 와인 한 잔을 곁들이는 그런 화려하면서도 예술적인 삶을 꿈꿔봅니다."
        ),
        ReplyModel(
            id = 5,
            image = R.drawable.img_temp_square_user_thumbnail,
            level = 5,
            date = "2025.12.24",
            question = "Q. 다시 태어난다면 어느 나라에서 태어나고 싶으신가요?",
            answer = "포르투갈의 리스본입니다. 노란 트램이 지나다니는 언덕길과 파란 타일이 아름다운 집들 사이에서, 바다 냄새를 맡으며 자유로운 영혼으로 살아가보고 싶다는 생각을 자주 하곤 해요."
        )
        // ... 나머지 데이터도 비슷한 형식으로 채우시면 됩니다.
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareAllRepliesBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        rvSquareAllReplies.adapter = adapter
        adapter.submitList(dummyData)
    }

    private fun initListeners() = with(binding) {
        ivSquareAllRepliesBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}