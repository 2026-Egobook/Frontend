package com.egobook.app.ui.diary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.FragmentDiaryListBinding
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.ui.diary.adapter.DiaryRVAdapter
import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding

    private val viewModel: DiariesViewModel by activityViewModels()
    private var tabPosition: Int = 0
    private val diaryRVAdapter = DiaryRVAdapter()
    
    // 스크롤 상태 콜백 인터페이스
    interface OnScrollListener {
        fun onScrollStateChanged(isScrolled: Boolean)
    }
    
    private var scrollListener: OnScrollListener? = null
    
    fun setOnScrollListener(listener: OnScrollListener) {
        scrollListener = listener
    }

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->

                    diaryRVAdapter.submitList(state.diaries)

                    val isEmpty = state.diaries.isEmpty()

                    binding.layoutEmpty.visibility =
                        if (isEmpty) View.VISIBLE else View.GONE

                    binding.rvDiary.visibility =
                        if (isEmpty) View.GONE else View.VISIBLE

                }
            }
        }
    }



    private fun initRecyclerView() {
        diaryRVAdapter.setMyItemClickListener(object :
            DiaryRVAdapter.MyItemClickListener {

            override fun onItemClick(diary: Diary) {
                // 💡 1. 부모 프래그먼트(DiaryFragment)가 생성한 Directions를 사용합니다.
                val action = DiaryFragmentDirections.actionDiaryFragmentToDiaryCheckFragment(
                    diaryId = diary.diaryId
                )
                // 💡 2. 부모 프래그먼트의 NavController로 action을 실행합니다.
                parentFragment?.findNavController()?.navigate(action)
            }
        })

        binding.rvDiary.adapter = diaryRVAdapter
        binding.rvDiary.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        
        // 스크롤 리스너 추가
        binding.rvDiary.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                val firstVisiblePosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
                
                // 첫 번째 아이템이 보이지 않으면 스크롤된 상태로 판단
                scrollListener?.onScrollStateChanged(firstVisiblePosition > 0)
            }
        })
    }
    
    // 맨 위로 스크롤하는 public 함수
    fun scrollToTop() {
        binding.rvDiary.smoothScrollToPosition(0)
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
