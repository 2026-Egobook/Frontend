package com.egobook.app.ui.diary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.databinding.FragmentDiaryListBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.ui.diary.adapter.DiaryRVAdapter
import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import com.egobook.app.ui.diary.util.toDateTimeString

class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding

    private val viewModel: DiariesViewModel by activityViewModels()
    private var tabPosition: Int = 0
    private val diaryRVAdapter = DiaryRVAdapter()

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

        initRecyclerView()
        observeDiaries()
    }

    private fun observeDiaries() {
        val types = getDiaryTypesByPosition(tabPosition)

        viewModel.state
            .map { it.diaries }
            .distinctUntilChanged()
            .onEach { diaries ->
                val filtered = diaries.filter { diary ->
                    types == null || diary.types.any { it in types }
                }
                diaryRVAdapter.submitList(filtered)
            }
            .launchIn(lifecycleScope) // Fragment의 lifecycleScope
    }

    private fun getDiaryTypesByPosition(position: Int): Set<DiaryType>? {
        return when(position) {
            0 -> null // 전체
            1 -> setOf(DiaryType.EMOTION)
            2 -> setOf(DiaryType.WORRY)
            3 -> setOf(DiaryType.PRAISE)
            4 -> setOf(DiaryType.THANKS)
            else -> null
        }
    }

    private fun initRecyclerView() {

        diaryRVAdapter.setMyItemClickListener(object :
            DiaryRVAdapter.MyItemClickListener {

            override fun onItemClick(diary: Diary) {
                val action =
                    DiaryFragmentDirections.actionDiaryFragmentToDiaryCheckFragment(
                        diaryContent = diary.content,
                        diaryTime = diary.time.toDateTimeString()
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
