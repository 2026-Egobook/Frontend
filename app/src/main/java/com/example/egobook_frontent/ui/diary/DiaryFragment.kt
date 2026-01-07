package com.example.egobook_frontent.ui.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentDiaryBinding

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private var diaryDatas = ArrayList<Diary>()

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
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }

    private fun setupDummyData() {
        diaryDatas.addAll(arrayListOf(
            Diary("요즘 너무 설렌당", null, "17:00"),
            Diary("요즘 너무 설렌당", null, "17:00"),
            Diary("요즘 너무 설렌당", null, "17:00"),
            Diary("요즘 너무 설렌당", null, "17:00"),
            Diary("요즘 너무 설렌당", null, "17:00")
        ))
    }

    private fun initRecyclerView() {
        val diaryRVAdapter = DiaryRVAdapter(diaryDatas)
        binding.rvDiary.adapter = diaryRVAdapter
        binding.rvDiary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
