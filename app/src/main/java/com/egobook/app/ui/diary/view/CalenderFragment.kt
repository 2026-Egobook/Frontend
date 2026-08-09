package com.egobook.app.ui.diary.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
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
import com.egobook.app.util.UiState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
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
        
        // 1. 초기 년월 결정 (인자 있으면 그걸로, 없으면 현재 달)
        val initialMonth = getInitialMonthFromArgs()
        
        setupDayOfWeekTitles()
        setupCalendar(initialMonth)
        
        // 2. 초기 월 데이터 로드 (스와이프 시 자동 호출되지만 초기 진입 시 한 번은 직접 호출)
        viewModel.setYearMonth(initialMonth)
        viewModel.logCalendarView()
        
        // 2. observing 시작
        observeViewModel()
        setupMonthDialogResultListener()

        binding.apply {
            btnList.setOnClickListener {
                findNavController().popBackStack()
            }
            tvMonth.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)

                val dialog = MonthDialogFragment.newInstance(
                    viewModel.selectedYearMonth.year
                )
                dialog.isCancelable = true
                dialog.show(childFragmentManager, "MonthDialog")
            }

            btnPrevMonth.setOnClickListener {
                binding.calendarView.scrollToMonth(viewModel.selectedYearMonth.minusMonths(1))
            }

            btnNextMonth.setOnClickListener {
                binding.calendarView.scrollToMonth(viewModel.selectedYearMonth.plusMonths(1))
            }
            btnExport.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)
                val dialog = DiaryExportDialogFragment()
                dialog.isCancelable = true
                dialog.show(childFragmentManager, "DiaryExportDialog")
            }
        }
    }

    /**
     * 초기 년월을 인자에서 추출 (없으면 현재 달 반환)
     */
    private fun getInitialMonthFromArgs(): YearMonth {
        val args = arguments
        val year = args?.getInt("year", -1) ?: -1
        val month = args?.getInt("month", -1) ?: -1

        return if (year != -1 && month != -1) {
            YearMonth.of(year, month)
        } else {
            YearMonth.now()
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
            // 다이얼로그가 닫힌 후 스와이프로 해당 월로 이동
            binding.calendarView.scrollToMonth(YearMonth.of(year, month))
        }
    }
    
    /**
     * ViewModel 상태 관찰
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    // 상단 텍스트 업데이트
                    binding.tvYear.text = state.selectedYearMonth.year.toString()
                    binding.tvMonth.text = "${state.selectedYearMonth.monthValue}월"
                    // 감정 데이터 변경 시 캘린더 뷰 갱신
                    binding.calendarView.notifyMonthChanged(state.selectedYearMonth)
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
    private fun setupCalendar(initialMonth: YearMonth) {
        initializeCalendarRange(initialMonth)
        initializeYearMonthText(initialMonth)
        setupDayBinder()
    }
    
    /**
     * 달력 범위 설정 (과거 100개월 ~ 미래 100개월)
     */
    private fun initializeCalendarRange(initialMonth: YearMonth) {
        val startMonth = initialMonth.minusMonths(100)
        val endMonth = initialMonth.plusMonths(100)
        val firstDayOfWeek = DayOfWeek.MONDAY
        
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(initialMonth)
        
        // 스와이프로 월 변경 시 API 호출
        binding.calendarView.monthScrollListener = { calendarMonth ->
            val newYearMonth = calendarMonth.yearMonth
            // 월이 변경되었을 때만 API 호출
            if (viewModel.selectedYearMonth != newYearMonth) {
                viewModel.setYearMonth(newYearMonth)
            }
        }
    }
    
    /**
     * 초기 연/월 텍스트 설정
     */
    private fun initializeYearMonthText(initialMonth: YearMonth) {
        binding.tvYear.text = initialMonth.year.toString()
        binding.tvMonth.text = "${initialMonth.monthValue}월"
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
        
        // 감정 이미지 바인딩 (API 데이터에서 조회)
        bindEmotionImage(container, data.date)
    }
    
    /**
     * 감정 이미지 바인딩
     */
    private fun bindEmotionImage(container: DayViewContainer, date: LocalDate) {
        val emotionLevel = viewModel.getEmotionLevel(date)
        
        Timber.tag("CalenderDebug").d("Date: $date, EmotionLevel: $emotionLevel, MapKeys: ${viewModel.state.value.dateEmotionMap.keys}")
        
        if (emotionLevel != null) {
            // 감정 레벨에 따른 이미지 설정 (1~5 유효, 그 외는 기본 이미지)
            val emotionDrawable = getEmotionDrawable(emotionLevel)
            container.binding.dayEmotionImg.setImageResource(emotionDrawable)
            container.binding.dayEmotionImg.visibility = View.VISIBLE
        } else {
            // 감정 기록 없음
            container.binding.dayEmotionImg.visibility = View.INVISIBLE
        }
    }
    
    /**
     * 감정 레벨에 따른 Drawable 리소스 반환
     */
    private fun getEmotionDrawable(level: Int): Int {
        return when (level) {
            1 -> R.drawable.img_emotion_very_sad // 오타 있는 파일명 그대로 사용
            2 -> R.drawable.img_emotion_sad
            3 -> R.drawable.img_emotion_neutral
            4 -> R.drawable.img_emotion_happy
            5 -> R.drawable.img_emotion_very_happy
            else -> R.drawable.img_emotion_neutral
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
