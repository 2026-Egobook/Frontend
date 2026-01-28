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
import androidx.navigation.fragment.findNavController
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentCalenderBinding
import com.egobook.app.ui.diary.adapter.DayViewContainer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!
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

        binding.apply {
            btnList.setOnClickListener {
                findNavController().popBackStack()
            }
            tvMonth.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE) // 블러 효과 적용

                val dialog = MonthDialogFragment()
                dialog.isCancelable = true
                dialog.show(childFragmentManager, "MonthDialog")
            }

            btnPrevMonth.setOnClickListener {
                binding.calendarView.findFirstVisibleMonth()?.let {
                    binding.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
                }
            }

            btnNextMonth.setOnClickListener {
                binding.calendarView.findFirstVisibleMonth()?.let {
                    binding.calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
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
        setupMonthScrollListener()
        setupDayBinder()
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
     * 월 스크롤 리스너 설정
     */
    private fun setupMonthScrollListener() {
        binding.calendarView.monthScrollListener = { month ->
            binding.tvYear.text = month.yearMonth.year.toString()
            binding.tvMonth.text = "${month.yearMonth.monthValue}월"
        }
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
            data.date == today -> bindTodayDate(container)
            else -> bindRegularDate(container, data.date)
        }
    }
    
    /**
     * 이전/다음 달 날짜 바인딩 (숨김 처리)
     */
    private fun bindOutOfMonthDate(container: DayViewContainer) {
        container.binding.calendarDayText.visibility = View.INVISIBLE
        container.binding.dayEmotionImg.visibility = View.INVISIBLE
    }
    
    /**
     * 오늘 날짜 바인딩
     */
    private fun bindTodayDate(container: DayViewContainer) {
        container.binding.calendarDayText.apply {
            visibility = View.VISIBLE
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.today_background)
        }
        container.binding.dayEmotionImg.visibility = View.GONE
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
