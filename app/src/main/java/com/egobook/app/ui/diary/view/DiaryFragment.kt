    package com.egobook.app.ui.diary.view

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.activityViewModels
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import androidx.navigation.fragment.findNavController
    import androidx.viewpager2.widget.ViewPager2
    import com.egobook.app.BlurLevel
    import com.egobook.app.R
    import com.egobook.app.applyScreenBlur
    import com.egobook.app.databinding.FragmentDiaryBinding
    import com.egobook.app.domain.model.DiaryType
    import com.egobook.app.ui.diary.adapter.DiaryVPAdapter
    import com.egobook.app.ui.diary.util.toDayOfMonthString
    import com.egobook.app.ui.diary.util.toMonthString
    import com.egobook.app.ui.diary.util.toYearString
    import com.egobook.app.ui.diary.viewmodel.DiariesEvent
    import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
    import com.google.android.material.tabs.TabLayout
    import com.google.android.material.tabs.TabLayoutMediator
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch
    import kotlin.getValue

    class DiaryFragment : Fragment() {
        private var _binding: FragmentDiaryBinding? = null
        private val binding get() = _binding!!

        private val viewModel: DiariesViewModel by activityViewModels()
        private var currentDiaryListFragment: DiaryListFragment? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?
        ): View {
            _binding = FragmentDiaryBinding.inflate(inflater, container, false)

            binding.vpDiary.apply {
                clipToPadding = true
                clipChildren = true
                offscreenPageLimit = 1
            }

            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // 초기에는 GoToTop 버튼 숨김
            binding.btnGoToTop.visibility = View.GONE
            
            initViewPager()
            setupClickListener()
            observeViewModel()
        }

        private fun setupClickListener() {
            binding.apply {
                btnAdd.setOnClickListener {
                    // 현재 선택된 날짜를 ISO 형식으로 변환하여 전달
                    val selectedDate = viewModel.state.value.selectedDate.toString()
                    val action = DiaryFragmentDirections.actionDiaryFragmentToDiaryWriteFragment(selectedDate)
                    findNavController().navigate(action)
                }
                btnCalender.setOnClickListener {
                    findNavController().navigate(R.id.action_diaryFragment_to_calenderFragment)
                }
                btnExport.setOnClickListener {
                    applyScreenBlur(BlurLevel.BASE)
                    val dialog = DiaryExportDialogFragment()
                    dialog.isCancelable = true
                    dialog.show(parentFragmentManager, "ConfirmDialog")
                }
                btnPrevDate.setOnClickListener {
                    val prevDate = viewModel.state.value.selectedDate.minusDays(1)
                    viewModel.onEvent(DiariesEvent.ChangeDate(prevDate))
                    binding.vpDiary.setCurrentItem(0, false) // "전체" 탭으로 이동
                }
                btnNextDate.setOnClickListener {
                    val nextDate = viewModel.state.value.selectedDate.plusDays(1)
                    viewModel.onEvent(DiariesEvent.ChangeDate(nextDate))
                    binding.vpDiary.setCurrentItem(0, false) // "전체" 탭으로 이동
                }
                btnGoToTop.setOnClickListener {
                    // 현재 보이는 DiaryListFragment의 RecyclerView를 맨 위로 스크롤
                    currentDiaryListFragment?.scrollToTop()
                }
            }
        }
        private fun observeViewModel() {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        binding.tvYear.text = state.selectedDate.toYearString()
                        binding.tvMonth.text = state.selectedDate.toMonthString()
                        binding.tvDate.text = state.selectedDate.toDayOfMonthString()

                    }
                }
            }
        }

        private fun initViewPager() {
            val adapter = DiaryVPAdapter(this)
            binding.vpDiary.adapter = adapter

            val tabTitles = listOf("전체", "감정", "고민", "칭찬", "감사")

            TabLayoutMediator(binding.tbType, binding.vpDiary) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

            // 탭 선택 이벤트 처리
            binding.tbType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val types = getDiaryTypesByPosition(tab.position)
                    viewModel.onEvent(DiariesEvent.SwipeTab(types))
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            
            // ViewPager 페이지 변경 리스너 추가
            binding.vpDiary.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateCurrentFragment(position)
                }
            })
            
            // 초기 Fragment 설정
            updateCurrentFragment(0)
        }
        
        private fun updateCurrentFragment(position: Int) {
            // ViewPager2에서 현재 보이는 Fragment 가져오기
            val fragment = childFragmentManager.findFragmentByTag("f$position") as? DiaryListFragment
            currentDiaryListFragment = fragment
            
            // 스크롤 리스너 설정
            fragment?.setOnScrollListener(object : DiaryListFragment.OnScrollListener {
                override fun onScrollStateChanged(isScrolled: Boolean) {
                    // 스크롤 상태에 따라 GoToTop 버튼 표시/숨김
                    binding.btnGoToTop.visibility = if (isScrolled) View.VISIBLE else View.GONE
                }
            })
        }

        private fun getDiaryTypesByPosition(position: Int): Set<com.egobook.app.domain.model.DiaryType>? {
            return when(position) {
                0 -> null // 전체
                1 -> setOf(DiaryType.EMOTION)
                2 -> setOf(DiaryType.WORRY)
                3 -> setOf(DiaryType.PRAISE)
                4 -> setOf(DiaryType.THANKS)
                else -> null
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }