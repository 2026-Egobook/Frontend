    package com.egobook.app.ui.diary.view
    
    import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.coroutines.delay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.egobook.app.ui.diary.model.ToastMessage
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

            // 캘린더에서 선택한 날짜가 있으면 적용 (없으면 마지막 선택 날짜 유지)
            applySelectedDateFromArgs()

            // SavedStateHandle로부터 토스트 메시지 확인 및 표시
            checkAndShowToastMessages()

            initViewPager()
            setupClickListener()
            observeViewModel()
        }
        
        /**
         * 캘린더에서 선택한 날짜 적용 (있는 경우에만)
         * 다른 화면에서는 마지막 선택된 날짜 유지
         */
        private fun applySelectedDateFromArgs() {
            val args = arguments
            
            val year = args?.getInt("selectedYear", -1) ?: -1
            val month = args?.getInt("selectedMonth", -1) ?: -1
            val day = args?.getInt("selectedDay", -1) ?: -1
            
            // 캘린더에서 유효한 날짜가 전달된 경우에만 적용
            if (year != -1 && month != -1 && day != -1) {
                viewModel.onEvent(
                    DiariesEvent.ChangeDate(
                        year = year,
                        month = month,
                        day = day
                    )
                )
                // "전체" 탭으로 이동
                binding.vpDiary.setCurrentItem(0, false)
            }
            
            // 인자 사용 후 초기화 (다음 진입 시 재적용 방지)
            arguments = null
        }
        
        override fun onResume() {
            super.onResume()
            // 다른 화면에서 돌아왔을 때 데이터 새로고침만 수행 (날짜는 유지)
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
                    val date = viewModel.state.value.selectedDate

                    val action = DiaryFragmentDirections
                        .actionDiaryFragmentToCalenderFragment(
                            year = date.year,
                            month = date.monthValue
                        )

                    findNavController().navigate(action)
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

        /**
         * SavedStateHandle로부터 토스트 메시지 확인 및 표시
         */
        private fun checkAndShowToastMessages() {
            val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
            val jsonMessages = savedStateHandle?.get<String>("toast_messages")

            if (!jsonMessages.isNullOrEmpty()) {
                val type = object : TypeToken<List<ToastMessage>>() {}.type
                val messages = Gson().fromJson<List<ToastMessage>>(jsonMessages, type)
                showToastMessages(messages)
                // 사용 후 삭제 (중복 표시 방지)
                savedStateHandle.remove<String>("toast_messages")
            }
        }

        /**
         * 토스트 메시지 리스트를 순차적으로 표시
         */
        private fun showToastMessages(messages: List<ToastMessage>) {
            if (messages.isEmpty()) return

            viewLifecycleOwner.lifecycleScope.launch {
                messages.forEachIndexed { index, message ->
                    when (message.rewardType) {
                        "INK" -> showInkToast(message.message, message.imageRes)
                        "REWARD" -> showRewardToast(message.message, message.imageRes)
                    }

                    // 연속 토스트 사이에 딜레이 (마지막 제외)
                    if (index < messages.size - 1) {
                        delay(2500) // 2.5초 딜레이
                    }
                }
            }
        }

        /**
         * 잉크 토스트 표시 (toast_ink.xml)
         */
        private fun showInkToast(message: String, imageRes: Int) {
            val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
            val customView = layoutInflater.inflate(R.layout.toast_ink, null)

            // 메시지 설정 (서버에서 내려준 메시지 그대로 사용)
            val tvMessage = customView.findViewById<TextView>(R.id.tv_message)
            tvMessage.text = message

            // 이미지 설정
            val ivInk = customView.findViewById<ImageView>(R.id.iv_ink)
            ivInk.setImageResource(imageRes)

            val layout = snackBar.view as ViewGroup
            layout.setPadding(0, 0, 0, 0)
            layout.setBackgroundColor(Color.TRANSPARENT)
            layout.addView(customView, 0)

            // BottomNav에 붙이기
            val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
            snackBar.anchorView = bottomNav

            // margin으로 띄우기
            val extra = (9 * resources.displayMetrics.density).toInt()
            val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin += extra
            snackBar.view.layoutParams = params

            snackBar.show()
        }

        /**
         * 리워드 토스트 표시 (toast_reward.xml)
         */
        private fun showRewardToast(message: String, imageRes: Int) {
            val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
            val customView = layoutInflater.inflate(R.layout.toast_reward, null)

            // 메시지 설정
            val tvMessage = customView.findViewById<TextView>(R.id.tv_message)
            tvMessage.text = message

            // 이미지 설정
            val ivSun = customView.findViewById<ImageView>(R.id.iv_sun)
            ivSun.setImageResource(imageRes)

            val layout = snackBar.view as ViewGroup
            layout.setPadding(0, 0, 0, 0)
            layout.setBackgroundColor(Color.TRANSPARENT)
            layout.addView(customView, 0)

            // BottomNav에 붙이기
            val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
            snackBar.anchorView = bottomNav

            // margin으로 띄우기
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