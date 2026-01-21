package com.example.egobook_frontent.ui.diary.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.BlurLevel
import com.example.egobook_frontent.R
import com.example.egobook_frontent.applyScreenBlur
import com.example.egobook_frontent.databinding.FragmentCandlerBinding
import com.example.egobook_frontent.ui.diary.adapter.DayViewContainer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth

class CandlerFragment : Fragment() {

    private var _binding: FragmentCandlerBinding? = null
    private val binding get() = _binding!!
    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCandlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

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

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = daysOfWeek().first()

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

                    // 오늘 날짜인지 확인
                    if (data.date == today) {
                        // 오늘 날짜이면: 초록색 배경, 흰색 텍스트, 감정 이미지는 숨김
                        container.binding.calendarDayText.setTextColor(Color.WHITE)
                        container.binding.calendarDayText.setBackgroundResource(R.drawable.today_background)
                        //container.binding.dayEmotionImg.visibility = View.GONE
                    } else {
                        // 오늘이 아니면: 배경 없음, 검은색 텍스트, 감정 이미지는 표시
                        container.binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.cos_black))
                        container.binding.calendarDayText.background = null
                        container.binding.dayEmotionImg.visibility = View.VISIBLE
                    }
                } else {
                    // 현재 달이 아닌 날짜는 모두 숨김
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
