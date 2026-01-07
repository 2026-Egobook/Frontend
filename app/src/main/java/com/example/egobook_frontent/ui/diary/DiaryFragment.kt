package com.example.egobook_frontent.ui.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentDiaryBinding

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    //더미데이터
    private var diaryDatas = ArrayList<Diary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)

        //더미데이터 삽입
        diaryDatas.apply {
            add(
                Diary(
                    content = "요즘 너무 설렌당",
                    type = null,
                    time = "17:00"
                )
            )
            add(
                Diary(
                    content = "요즘 너무 설렌당",
                    type = null,
                    time = "17:00"
                )
            )
            add(
                Diary(
                    content = "요즘 너무 설렌당",
                    type = null,
                    time = "17:00"
                )
            )
            add(
                Diary(
                    content = "요즘 너무 설렌당",
                    type = null,
                    time = "17:00"
                )
            )
            add(
                Diary(
                    content = "요즘 너무 설렌당",
                    type = null,
                    time = "17:00"
                )
            )
        }
        return binding.root
    }

    //아 죄송합니다 커밋 메세지 실수했어요...

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 일기 추가 버튼 클릭 리스너
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
