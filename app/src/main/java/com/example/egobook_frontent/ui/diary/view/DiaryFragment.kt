package com.example.egobook_frontent.ui.diary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentDiaryBinding
import com.example.egobook_frontent.ui.diary.Diary
import com.example.egobook_frontent.ui.diary.adapter.DiaryRVAdapter

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val diaryDatas: MutableList<Diary> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 데이터가 비어있을 때만 더미데이터 삽입 (중복 방지)
        if (diaryDatas.isEmpty()) {
            setupDummyData()
        }

        // 2. 어댑터 설정
        initRecyclerView()

        // 3. 버튼 클릭 리스너
        binding.apply {
            btnAdd.setOnClickListener {
                findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
            btnCalender.setOnClickListener {
                findNavController().navigate(R.id.action_diaryFragment_to_candlerFragment)
            }
        }
    }

    private fun setupDummyData() {
        diaryDatas.addAll(arrayListOf(
            Diary("테스트1", null, "17:00"),
            Diary("테스트2", null, "17:00"),
            Diary("테스트3", null, "17:00"),
            Diary("테스트4", null, "17:00"),
            Diary("테스트5", null, "17:00")
        ))
    }

    private fun initRecyclerView() {
        val diaryRVAdapter = DiaryRVAdapter(diaryDatas)
        diaryRVAdapter.setMyItemClickListener(object :
            DiaryRVAdapter.MyItemClickListener {
                // 💡 1. 어댑터와 동일한 파라미터를 받도록 수정합니다.
                override fun onItemClick(diary: Diary) {
                    // 2. Safe Args가 생성한 Directions 클래스를 사용합니다.
                    val action = DiaryFragmentDirections.actionDiaryFragmentToDiaryCheckFragment(
                        // 💡 3. 클릭된 아이템(diary)의 데이터를 전달합니다.
                        diaryContent = diary.content,
                        diaryTime = diary.time
                    )
                    // 4. action 객체를 사용하여 안전하게 화면을 전환합니다.
                    findNavController().navigate(action)
                }
            })

        binding.rvDiary.adapter = diaryRVAdapter
        binding.rvDiary.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}