package com.egobook.app.ui.diary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentDiaryListBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.ui.diary.adapter.DiaryRVAdapter
import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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

    // [수정] observeDiaries 로직 전체 변경
    private fun observeDiaries() {
        // Fragment의 View 생명주기를 따르도록 수정
        viewLifecycleOwner.lifecycleScope.launch {
            // Fragment가 STARTED 상태일 때만 Flow를 구독
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel의 state 전체를 관찰
                viewModel.state.collectLatest { state ->
                    // ViewModel에서 이미 필터링된 diaries 리스트를 어댑터에 바로 전달
                    // 이제 날짜가 바뀌거나 탭이 바뀌면 항상 최신 목록을 받아서 표시함
                    diaryRVAdapter.submitList(state.diaries)
                }
            }
        }
    }

//    private fun getDiaryTypesByPosition(position: Int): Set<DiaryType>? {
//        return when(position) {
//            0 -> null // 전체
//            1 -> setOf(DiaryType.EMOTION)
//            2 -> setOf(DiaryType.WORRY)
//            3 -> setOf(DiaryType.PRAISE)
//            4 -> setOf(DiaryType.THANKS)
//            else -> null
//        }
//    }

    private fun initRecyclerView() {
        diaryRVAdapter.setMyItemClickListener(object :
            DiaryRVAdapter.MyItemClickListener {

            override fun onItemClick(diary: Diary) {
                // 💡 1. 부모 프래그먼트(DiaryFragment)가 생성한 Directions를 사용합니다.
                val action = DiaryFragmentDirections.actionDiaryFragmentToDiaryCheckFragment(
                    diaryId = diary.id
                )
                // 💡 2. 부모 프래그먼트의 NavController로 action을 실행합니다.
                parentFragment?.findNavController()?.navigate(action)
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
