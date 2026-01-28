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

        // 💡 1. 동적 요일 헤더 설정 함수 호출
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

    // 요일 헤더를 동적으로 설정하는 함수 추가 (월화수목금토일 순서)
    private fun setupDayOfWeekTitles() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) // 월요일부터 시작
        // 💡 1. <include> 태그의 ID(titles_layout)를 통해 먼저 접근합니다.
        // 💡 2. 그 다음, 그 안의 LinearLayout(titlesContainer)을 찾습니다.
        binding.titlesLayout.titlesContainer.children.forEachIndexed { index, view ->
            if (view is TextView) {
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                view.text = title
                
                // 일요일은 빨간색으로 표시
                if (dayOfWeek == DayOfWeek.SUNDAY) {
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.cos_red))
                } else {
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_bage_text))
                }
            }
        }
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = DayOfWeek.MONDAY // 월요일부터 시작

        binding.tvYear.text = currentMonth.year.toString()
        binding.tvMonth.text = "${currentMonth.monthValue}월"

        binding.calendarView.monthScrollListener = { month ->
            binding.tvYear.text = month.yearMonth.year.toString()
            binding.tvMonth.text = "${month.yearMonth.monthValue}월"
        }

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.binding.calendarDayText.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.binding.calendarDayText.visibility = View.VISIBLE

                    if (data.date == today) {
                        // 오늘 날짜
                        container.binding.calendarDayText.setTextColor(Color.WHITE)
                        container.binding.calendarDayText.setBackgroundResource(R.drawable.today_background)
                        container.binding.dayEmotionImg.visibility = View.GONE // 오늘 날짜에는 감정 이미지 숨김
                    } else {
                        // 일반 날짜
                        container.binding.calendarDayText.background = null
                        container.binding.dayEmotionImg.visibility = View.VISIBLE
                        
                        // 일요일은 빨간색, 그 외는 검정색
                        if (data.date.dayOfWeek == DayOfWeek.SUNDAY) {
                            container.binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.cos_red))
                        } else {
                            container.binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.cos_black))
                        }
                    }
                } else {
                    container.binding.calendarDayText.visibility = View.INVISIBLE
                    container.binding.dayEmotionImg.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
