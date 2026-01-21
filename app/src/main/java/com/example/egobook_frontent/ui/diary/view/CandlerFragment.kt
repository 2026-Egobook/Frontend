package com.example.egobook_frontent.ui.diary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.BlurLevel
import com.example.egobook_frontent.applyScreenBlur
import com.example.egobook_frontent.databinding.FragmentCandlerBinding
import com.example.egobook_frontent.ui.diary.adapter.DayViewContainer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.YearMonth

class CandlerFragment : Fragment() {

    private var _binding: FragmentCandlerBinding? = null
    private val binding get() = _binding!!

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

            // 💡 이전 달로 스크롤
            btnPrevMonth.setOnClickListener {
                binding.calendarView.findFirstVisibleMonth()?.let {
                    binding.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
                }
            }

            // 💡 다음 달로 스크롤
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
                    container.binding.dayEmotionImg.visibility = View.VISIBLE
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
