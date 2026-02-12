package com.egobook.app.ui.diary.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentCalenderBinding
import com.egobook.app.ui.diary.adapter.DayViewContainer
import com.egobook.app.ui.diary.viewmodel.CalenderViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalenderViewModel by viewModels()
    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setupDayOfWeekTitles()
        setupCalendar()
        observeViewModel()
        setupMonthDialogResultListener()

        binding.apply {
            btnList.setOnClickListener {
                findNavController().popBackStack()
            }
            tvMonth.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)

                val dialog = MonthDialogFragment.newInstance(
                    viewModel.selectedYearMonth.value.year
                )
                dialog.isCancelable = true
                dialog.show(childFragmentManager, "MonthDialog")
            }

            btnPrevMonth.setOnClickListener {
                viewModel.selectedYearMonth.value.minusMonths(1).let {
                    viewModel.setYearMonth(it)
                }
            }

            btnNextMonth.setOnClickListener {
                viewModel.selectedYearMonth.value.plusMonths(1).let {
                    viewModel.setYearMonth(it)
                }
            }
        }
    }
    
    /**
     * MonthDialogFragment에서 월 선택 결과 수신
     */
    private fun setupMonthDialogResultListener() {
        childFragmentManager.setFragmentResultListener(
            MonthDialogFragment.REQUEST_KEY_MONTH_SELECTED,
            viewLifecycleOwner
        ) { _, bundle ->
            val year = bundle.getInt(MonthDialogFragment.BUNDLE_KEY_YEAR)
            val month = bundle.getInt(MonthDialogFragment.BUNDLE_KEY_MONTH)
            // 다이얼로그가 닫힌 후 ViewModel 업데이트 → 캘린더 스크롤
            viewModel.setYearMonth(YearMonth.of(year, month))
        }
    }
    
    /**
     * ViewModel 상태 관찰 - 스와이프 없이 해당 월 즉시 표시
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedYearMonth.collectLatest { yearMonth ->
                    // 애니메이션 없이 해당 월 즉시 이동
                    binding.calendarView.scrollToMonth(yearMonth)
                    // 상단 텍스트 업데이트
                    binding.tvYear.text = yearMonth.year.toString()
                    binding.tvMonth.text = "${yearMonth.monthValue}월"
                }
            }
        }
    }

    // ========== 달력 초기화 ==========
    
    /**
     * 요일 헤더 설정 (월화수목금토일 순서)
     */
    private fun setupDayOfWeekTitles() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        
        binding.titlesLayout.titlesContainer.children.forEachIndexed { index, view ->
            if (view is TextView) {
                val dayOfWeek = daysOfWeek[index]
                view.text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                view.setTextColor(getDayOfWeekColor(dayOfWeek))
            }
        }
    }
    
    /**
     * 달력 설정 및 초기화
     */
    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        
        initializeCalendarRange(currentMonth)
        initializeYearMonthText(currentMonth)
        setupDayBinder()
        
        // 초기 ViewModel 설정
        viewModel.setYearMonth(currentMonth)
    }
    
    /**
     * 달력 범위 설정 (과거 100개월 ~ 미래 100개월)
     */
    private fun initializeCalendarRange(currentMonth: YearMonth) {
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = DayOfWeek.MONDAY
        
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
    }
    
    /**
     * 초기 연/월 텍스트 설정
     */
    private fun initializeYearMonthText(currentMonth: YearMonth) {
        binding.tvYear.text = currentMonth.year.toString()
        binding.tvMonth.text = "${currentMonth.monthValue}월"
    }

    /**
     * 날짜 바인더 설정
     */
    private fun setupDayBinder() {
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                bindCalendarDay(container, data)
            }
        }
    }
    
    // ========== 날짜 바인딩 로직 ==========
    
    /**
     * 날짜 셀 바인딩
     */
    private fun bindCalendarDay(container: DayViewContainer, data: CalendarDay) {
        container.binding.calendarDayText.text = data.date.dayOfMonth.toString()
        
        when {
            data.position != DayPosition.MonthDate -> bindOutOfMonthDate(container)
            data.date == today -> bindTodayDate(container, data.date)
            else -> bindRegularDate(container, data.date)
        }
    }
    
    /**
     * 이전/다음 달 날짜 바인딩 (숨김 처리)
     */
    private fun bindOutOfMonthDate(container: DayViewContainer) {
        container.binding.calendarDayText.visibility = View.INVISIBLE
        container.binding.dayEmotionImg.visibility = View.INVISIBLE
        container.view.setOnClickListener(null)
    }
    
    /**
     * 오늘 날짜 바인딩
     */
    private fun bindTodayDate(container: DayViewContainer, date: LocalDate) {
        container.binding.calendarDayText.apply {
            visibility = View.VISIBLE
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.today_background)
        }
        container.binding.dayEmotionImg.visibility = View.GONE
        
        // 오늘 날짜 클릭 리스너 설정
        container.view.setOnClickListener {
            navigateToDiaryWithDate(date)
        }
    }
    
    /**
     * 일반 날짜 바인딩
     */
    private fun bindRegularDate(container: DayViewContainer, date: LocalDate) {
        container.binding.calendarDayText.apply {
            visibility = View.VISIBLE
            background = null
            setTextColor(getDateTextColor(date))
        }
        container.binding.dayEmotionImg.visibility = View.VISIBLE
        
        // 일반 날짜 클릭 리스너 설정
        container.view.setOnClickListener {
            navigateToDiaryWithDate(date)
        }
    }
    
    /**
     * 선택한 날짜를 DiaryFragment로 전달하고 이동
     */
    private fun navigateToDiaryWithDate(date: LocalDate) {
        val action = CalenderFragmentDirections.actionCalenderFragmentToDiaryFragment(
            selectedYear = date.year,
            selectedMonth = date.monthValue,
            selectedDay = date.dayOfMonth
        )
        findNavController().navigate(action)
    }
    
    // ========== 색상 헬퍼 함수 ==========
    
    /**
     * 요일 색상 반환 (일요일: 빨강, 그 외: 기본)
     */
    private fun getDayOfWeekColor(dayOfWeek: DayOfWeek): Int {
        return if (dayOfWeek == DayOfWeek.SUNDAY) {
            ContextCompat.getColor(requireContext(), R.color.cos_red)
        } else {
            ContextCompat.getColor(requireContext(), R.color.dark_bage_text)
        }
    }
    
    /**
     * 날짜 텍스트 색상 반환 (일요일: 빨강, 그 외: 검정)
     */
    private fun getDateTextColor(date: LocalDate): Int {
        return if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            ContextCompat.getColor(requireContext(), R.color.cos_red)
        } else {
            ContextCompat.getColor(requireContext(), R.color.cos_black)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
