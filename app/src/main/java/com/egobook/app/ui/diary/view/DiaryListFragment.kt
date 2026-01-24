package com.egobook.app.ui.diary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.databinding.FragmentDiaryListBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.ui.diary.adapter.DiaryRVAdapter

class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding
    private val diaryDatas = mutableListOf<Diary>()

    private var tabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabPosition = arguments?.getInt(ARG_TAB_POSITION) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiaryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDummyData(tabPosition)
        initRecyclerView()
    }

    private fun setupDummyData(position: Int) {
        if (diaryDatas.isNotEmpty()) return

        when (position) {
            0 -> diaryDatas.addAll(
                listOf(
                    Diary("전체1", null, "17:00"),
                    Diary("전체2", null, "17:00"),
                    Diary("전체3", null, "17:00")
                )
            )
            1 -> diaryDatas.addAll(
                listOf(
                    Diary("감정1", null, "17:00"),
                    Diary("감정2", null, "17:00")
                )
            )
            2 -> diaryDatas.addAll(
                listOf(
                    Diary("고민1", null, "17:00")
                )
            )
            else -> diaryDatas.addAll(
                listOf(
                    Diary("기타1", null, "17:00")
                )
            )
        }
    }

    private fun initRecyclerView() {
        val diaryRVAdapter = DiaryRVAdapter(diaryDatas)

        diaryRVAdapter.setMyItemClickListener(object :
            DiaryRVAdapter.MyItemClickListener {

            override fun onItemClick(diary: Diary) {
                val action =
                    DiaryFragmentDirections.actionDiaryFragmentToDiaryCheckFragment(
                        diaryContent = diary.content,
                        diaryTime = diary.time
                    )
                findNavController().navigate(action)
            }
        })

        binding.rvDiary.adapter = diaryRVAdapter
        binding.rvDiary.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        private const val ARG_TAB_POSITION = "tab_position"

        fun newInstance(position: Int): DiaryListFragment {
            val fragment = DiaryListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_TAB_POSITION, position)
            }
            return fragment
        }
    }
}
