    package com.egobook.app.ui.diary.view

    import android.os.Bundle
    import android.view.Gravity
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import android.graphics.Color
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
    import com.egobook.app.ui.diary.adapter.DiaryVPAdapter
    import com.egobook.app.ui.diary.viewmodel.DiariesEvent
    import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
    import com.google.android.material.snackbar.Snackbar
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
        
        override fun onResume() {
            super.onResume()
            //다른 프래그먼트에서 돌아왔을 때 데이터 새로고침
            viewModel.onEvent(DiariesEvent.RefreshDiaries)
        }

        private fun setupClickListener() {
            binding.apply {
                btnAdd.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        // 캐시 기반 dailyCount 조회 (캐시 없으면 API 호출)
                        val dailyCount = viewModel.getDailyCountWithCache()

                        if (dailyCount >= 48) {
                            showCustomToast()
                            return@launch
                        }

                        // 48 미만일 때만 일기 작성 화면으로 이동
                        val selectedDate = viewModel.state.value.selectedDate.toString()
                        val action = DiaryFragmentDirections.actionDiaryFragmentToDiaryWriteFragment(selectedDate)
                        findNavController().navigate(action)
                    }
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
                    viewModel.onEvent(
                        DiariesEvent.ChangeDate(
                            year = prevDate.year,
                            month = prevDate.monthValue,
                            day = prevDate.dayOfMonth
                        )
                    )
                    binding.vpDiary.setCurrentItem(0, false) // "전체" 탭으로 이동
                }
                btnNextDate.setOnClickListener {
                    val nextDate = viewModel.state.value.selectedDate.plusDays(1)
                    viewModel.onEvent(
                        DiariesEvent.ChangeDate(
                            year = nextDate.year,
                            month = nextDate.monthValue,
                            day = nextDate.dayOfMonth
                        )
                    )
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
                        binding.tvYear.text = state.yearText
                        binding.tvMonth.text = "${state.monthText}월"
                        binding.tvDate.text = state.dayText
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
                    val displayTypes = getDisplayTypesByPosition(tab.position)
                    viewModel.onEvent(DiariesEvent.SwipeTab(displayTypes))
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

        private fun getDisplayTypesByPosition(position: Int): Set<String>? {
            return when(position) {
                0 -> null // 전체
                1 -> setOf("감정")
                2 -> setOf("고민")
                3 -> setOf("칭찬")
                4 -> setOf("감사")
                else -> null
            }
        }

        private fun showCustomToast() {
            val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)

            val customView = layoutInflater.inflate(R.layout.toast_over_write, null)

            val layout = snackBar.view as ViewGroup
            layout.setPadding(0, 0, 0, 0)
            layout.setBackgroundColor(Color.TRANSPARENT)

            layout.addView(customView, 0)

            // BottomNav에 붙이기
            val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
            snackBar.anchorView = bottomNav

            // translationY 대신 margin으로 띄우기
            val extra = (9 * resources.displayMetrics.density).toInt()
            val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin += extra
            snackBar.view.layoutParams = params

            snackBar.show()
        }



        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }