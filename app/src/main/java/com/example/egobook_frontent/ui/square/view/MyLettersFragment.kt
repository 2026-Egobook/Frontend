package com.example.egobook_frontent.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentMyLettersBinding
import com.example.egobook_frontent.ui.square.adapter.MyLettersAdapter
import com.example.egobook_frontent.ui.square.model.LetterModel
import com.example.egobook_frontent.ui.square.model.ReceivedModel

class MyLettersFragment : Fragment(R.layout.fragment_my_letters) {
    private lateinit var binding: FragmentMyLettersBinding
    private val adapter = MyLettersAdapter {
        val action = MyLettersFragmentDirections.actionMyLettersFragmentToMyLetterDetailFragment(letterItem = it)
        findNavController().navigate(action)
    }
    private val dummyData = listOf(
        LetterModel(
            id = 1,
            dateTime = "2025.01.20",
            sentContent = "오늘 문득 내가 정말 잘 살고 있는지 의문이 들었어. 매일 똑같은 일상을 반복하다 보니 내 마음이 어디로 향하고 있는지 놓치고 있었던 것 같아. 그래서 오늘은 아주 오랜만에 나에게 집중하는 시간을 가져보려고 해.",
            receivedContent = ReceivedModel(
                senderNickname = "따뜻한 고북이",
                receiverNickname = "나긋한 고북이",
                letterContent = "따뜻한 편지 고마워요. 저도 요즘 비슷한 고민을 하고 있었는데 보내주신 글을 읽고 큰 위로를 받았어요. 우리 천천히, 하지만 꾸준히 나아가 봐요."
            )
        ),
        LetterModel(
            id = 2,
            dateTime = "2025.01.18",
            sentContent = "요즘 날씨가 부쩍 추워졌는데 건강하게 잘 지내고 있니? 나는 오늘 길을 걷다 예쁘게 핀 겨울꽃을 봤어. 모진 추위 속에서도 꿋꿋하게 피어난 꽃을 보니 문득 네 생각이 나더라.",
            receivedContent = ReceivedModel(
                senderNickname = "용기있는 고북이",
                receiverNickname = "나긋한 고북이",
                letterContent = "보내주신 꽃 이야기에 마음이 몽글몽글해졌어요. 사실 오늘 조금 지쳐 있었는데, 다시 힘을 낼 수 있을 것 같아요. 당신도 감기 조심하세요!"
            )
        ),
        LetterModel(
            id = 3,
            dateTime = "2025.01.15",
            sentContent = "누군가에게 내 속마음을 말한다는 게 참 어려운 일인데, 익명의 힘을 빌려 너에게 고백해봐. 나는 사실 남들의 시선을 너무 많이 신경 쓰며 살고 있어. 너는 너만의 확고한 기준이 있니?",
            receivedContent = ReceivedModel(
                senderNickname = "지혜로운 고북이",
                receiverNickname = "나긋한 고북이",
                letterContent = "자신의 모습을 마주하는 것부터가 시작이에요. 남들이 뭐라 하든 내가 행복한 일을 찾아보세요. 당신은 충분히 멋진 사람입니다."
            )
        ),
        LetterModel(
            id = 4,
            dateTime = "2025.01.10",
            sentContent = "오늘은 내가 가장 좋아하는 책의 한 구절을 너에게 공유해주고 싶어. '삶은 속도가 아니라 방향이다'라는 말 들어본 적 있니? 조금 늦더라도 우리가 원하는 방향으로 가고 있다면 충분해.",
            receivedContent = ReceivedModel(
                senderNickname = "차분한 고북이",
                receiverNickname = "나긋한 고북이",
                letterContent = "그 문장, 저도 정말 좋아해요! 속도에 치여 살다 보면 소중한 걸 놓치기 쉬운데, 방향을 잘 잡고 있다면 잠시 쉬어가도 괜찮은 것 같아요."
            )
        ),
        LetterModel(
            id = 5,
            dateTime = "2025.01.05",
            sentContent = "새해 계획은 잘 실천하고 있니? 나는 거창한 계획 대신 '하루에 한 번 나 칭찬하기'를 목표로 세웠어. 사소한 일이라도 나를 격려해주니 자존감이 조금씩 올라가는 기분이 들어.",
            receivedContent = ReceivedModel(
                senderNickname = "다정한 고북이",
                receiverNickname = "나긋한 고북이",
                letterContent = "정말 멋진 목표네요! 저도 오늘부터 따라 해봐야겠어요. 자책 대신 '포기하지 않은 나'를 칭찬해주는 하루가 되어볼게요. 감사합니다."
            )
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyLettersBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        rvMyLetters.adapter = adapter
        adapter.submitList(dummyData)
    }

    private fun initListeners() = with(binding) {
        ivMyLettersBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}