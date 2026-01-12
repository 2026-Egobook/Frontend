package com.example.egobook_frontent.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCandlerBinding
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

        // 하단 시스템 바 영역만큼 패딩 주기
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 기존 패딩은 유지하면서 하단만 시스템 바 높이만큼 추가
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setupCalendar()

        // 클릭 리스너
        binding.apply {
            // 리스트 버튼 클릭 시 이전 화면(DiaryFragment)으로 이동
            btnList.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = daysOfWeek().first()

        // 초기 현재 날짜 기준 텍스트 설정
        binding.tvYear.text = currentMonth.year.toString()
        binding.tvMonth.text = "${currentMonth.monthValue}월"

        // 달력을 넘길 때마다 헤더(년, 월)를 업데이트하는 리스너
        binding.calendarView.monthScrollListener = { month ->
            binding.tvYear.text = month.yearMonth.year.toString()
            binding.tvMonth.text = "${month.yearMonth.monthValue}월"
        }

        // 달력 범위 및 시작 요일 설정
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        // MonthDayBinder 설정: 각 날짜 칸에 데이터를 그려줍니다.
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                // 날짜 숫자 표시
                container.binding.calendarDayText.text = data.date.dayOfMonth.toString()

                // 현재 달의 날짜만 보여주고, 이전/다음 달의 빈 칸은 숨기기
                if (data.position == DayPosition.MonthDate) {
                    container.binding.calendarDayText.visibility = View.VISIBLE
                    // 일기 데이터 유무에 따라 이미지 노출 (임시로 모두 표시)
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
