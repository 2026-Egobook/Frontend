package com.egobook.app.ui.counseling.view

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomStatisticsBinding
import com.egobook.app.domain.model.EmotionType
import com.egobook.app.ui.counseling.model.StatisticsModel
import com.egobook.app.ui.counseling.viewmodel.StatisticsViewModel
import com.egobook.app.util.UiState
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class EgoRoomStatisticsFragment : Fragment(R.layout.fragment_ego_room_statistics) {
    private lateinit var binding: FragmentEgoRoomStatisticsBinding
    private val viewModel: StatisticsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomStatisticsBinding.bind(view)
        initViews()
        fetchData()
        initObservers()
    }

    private fun initViews() = with(binding) {
        initTotalCountChart()
        initDailyStackedBarChart()
        initMonthlyTrendLineChart()
        // 상단 카드 텍스트 설정
        tvCounselingStatisticsDailyRecordPeakPositiveTime.text = getPickTime(true)
        tvCounselingStatisticsDailyRecordPeakNegativeTime.text = getPickTime(false)
        initWordBubbleChart()
    }

    private fun fetchData() {
        viewModel.fetchStatistics()
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statisticsInfo.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<StatisticsModel> -> {
                            val data = state.data
                        }
                    }
                }
            }
        }
    }

    private fun initTotalCountChart() = with(binding) {
        val veryGoodTotalCnt = 584
        val goodTotalCnt = 214
        val normalTotalCnt = 340
        val badTotalCnt = 148
        val veryBadTotalCnt = 54

        val counts = listOf(veryGoodTotalCnt, goodTotalCnt, normalTotalCnt, badTotalCnt, veryBadTotalCnt)
        val maxCount = counts.maxOrNull()?.toFloat() ?: 0f

        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountVeryHappy, value = veryGoodTotalCnt.toFloat(), label = "VeryHappy", colorRes = R.color.emotion_very_happy, maxValue = maxCount)
        tvCounselingStatisticsTotalCountVeryHappyCount.text = "${veryGoodTotalCnt}회"

        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountHappy, value = goodTotalCnt.toFloat(), label = "Happy", colorRes = R.color.emotion_happy, maxValue = maxCount)
        tvCounselingStatisticsTotalCountHappyCount.text = "${goodTotalCnt}회"

        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountNeutral, value = normalTotalCnt.toFloat(), label = "Neutral", colorRes = R.color.emotion_neutral, maxValue = maxCount)
        tvCounselingStatisticsTotalCountNeutralCount.text = "${normalTotalCnt}회"

        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountSad, value = badTotalCnt.toFloat(), label = "Sad", colorRes = R.color.emotion_sad, maxValue = maxCount)
        tvCounselingStatisticsTotalCountSadCount.text = "${badTotalCnt}회"

        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountVerySad, value = veryBadTotalCnt.toFloat(), label = "VerySad", colorRes = R.color.emotion_very_sad, maxValue = maxCount)
        tvCounselingStatisticsTotalCountVerySadCount.text = "${veryBadTotalCnt}회"
    }

    private fun initDailyStackedBarChart() = with(binding) {
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

        // 1. 더미 데이터 (합계 100으로 고정)
        val dummyData = listOf(
            floatArrayOf(15f, 20f, 30f, 20f, 15f), // 월
            floatArrayOf(10f, 15f, 40f, 25f, 10f), // 화
            floatArrayOf(20f, 25f, 25f, 20f, 10f), // 수
            floatArrayOf(5f, 10f, 35f, 30f, 20f),  // 목
            floatArrayOf(10f, 10f, 20f, 30f, 30f), // 금
            floatArrayOf(5f, 5f, 15f, 35f, 40f),   // 토
            floatArrayOf(20f, 10f, 20f, 25f, 25f)  // 일
        )

        val entries = dummyData.mapIndexed { index, values ->
            BarEntry(index.toFloat(), values)
        }

        val dataSet = BarDataSet(entries, "요일별 감정 분포").apply {
            colors = listOf(
                resources.getColor(R.color.emotion_very_sad, null),
                resources.getColor(R.color.emotion_sad, null),
                resources.getColor(R.color.emotion_neutral, null),
                resources.getColor(R.color.emotion_happy, null),
                resources.getColor(R.color.emotion_very_happy, null)
            )
            setDrawValues(false)
        }

        with(bcCounselingStatisticsDailyRecord) {
            data = BarData(dataSet).apply {
                barWidth = 0.4f // 막대 두께를 조금 더 슬림하게 조정 (선택 사항)
            }

            // X축 설정
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dayLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = resources.getColor(R.color.stacked_bar_chart_text, null)
                textSize = 13f
                granularity = 1f

                // ★ 핵심 수정 부분: 막대와 글자 사이의 간격 (단위: dp)
                yOffset = 12f
            }

            // Y축 및 기타 설정
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = 100f // 모든 막대 높이 동일하게 고정
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)

            // 차트 전체 하단 여백 추가 (라벨이 잘리지 않도록)
            setExtraOffsets(0f, 0f, 0f, 15f)

            invalidate()
        }

        // 하단 더미 텍스트
        tvCounselingStatisticsDailyRecordPeakPositiveTime.text = "토 14시"
        tvCounselingStatisticsDailyRecordPeakNegativeTime.text = "수 09시"
    }

    /**
     * 월별 평균 감정 점수를 보여주려면 감정당 가중치를 줘야한다.
     */
    private fun initMonthlyTrendLineChart() = with(binding) {
        // 1. 하단 요약 정보 더미 데이터 (이미지 기준)
        val lastMonthScore = 3.2f
        val currentMonthScore = 4.3f
        val diff = currentMonthScore - lastMonthScore

        tvCounselingStatisticsAverageLastMonthLabel.text = "1월 평균 점수"
        tvCounselingStatisticsAverageLastMonthValue.text = "$lastMonthScore"
        tvCounselingStatisticsAverageCurrentMonthLabel.text = "2월 평균 점수"
        tvCounselingStatisticsAverageCurrentMonthValue.text = "$currentMonthScore"

        val boldText = "${String.format("%.1f", abs(diff))} 상승" // "1.1 상승"
        val normalTextStart = "지난달 보다 "
        val normalTextEnd = "했어요"

        // 2. SpannableStringBuilder를 사용하여 텍스트를 조합합니다.
        val spannable = SpannableStringBuilder(normalTextStart + boldText + normalTextEnd)

        // 3. 굵게 만들 부분의 시작과 끝 인덱스를 계산합니다.
        val start = normalTextStart.length
        val end = start + boldText.length

        // 4. StyleSpan(Typeface.BOLD)을 적용합니다.
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 5. 완성된 Spannable을 TextView에 설정합니다.
        tvCounselingStatisticsAverageComparison.text = spannable

        // 2. 6개월치 더미 데이터 (이미지 그래프 흐름과 유사하게)
        // Y값은 1(매우나쁨) ~ 5(매우좋음) 사이
        val entries = listOf(
            Entry(0f, 2.2f), // 6월
            Entry(1f, 3.1f), // 7월
            Entry(2f, 4.2f), // 8월
            Entry(3f, 3.5f), // 9월
            Entry(4f, 4.3f), // 10월
            Entry(5f, 4.5f)  // 11월
        )

        val monthLabels = listOf("9월", "10월", "11월", "12월", "1월", "2월")

        val dataSet = LineDataSet(entries, "평균 점수").apply {
            color = resources.getColor(R.color.brand, null) // 초록색 계열
            setCircleColor(resources.getColor(R.color.brand, null))
            lineWidth = 2.5f
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR // 꺾은선
        }

        with(lcCounselingStatisticsTrend) {
            data = LineData(dataSet)

            // X축 설정
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                // 마지막 라벨(11월)만 보이게 하거나 간격을 넓히려면 추가 설정 가능
                yOffset = 10f
            }

            // Y축 설정 (이모지 위치와 맞추기 위해 1~5 고정)
            axisLeft.apply {
                axisMinimum = 1f
                axisMaximum = 5f
                setLabelCount(5, true) // 1, 2, 3, 4, 5 다섯 지점
                setDrawGridLines(true) // 가로선 표시
                gridColor = resources.getColor(R.color.neutral_stroke, null) // 연한 회색 가로선
                setDrawAxisLine(false)
                setDrawLabels(false) // 숫자는 숨기고 옆에 배치된 이모지로 대체
            }

            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)

            // 차트 오른쪽 끝에 '11월' 텍스트가 잘리지 않도록 여백
            setExtraOffsets(10f, 0f, 20f, 10f)

            invalidate()
        }
    }

    private fun getPickTime(isPositive: Boolean): String {
        // 실제 로직 대신 UI 확인을 위한 더미 데이터 반환
        return if (isPositive) "금 17시" else "월 08시"
    }

    // enum class로 빼기. 1~10 data class
    private fun initWordBubbleChart() = with(binding) {
        // 1. 색상 리스트 준비 (전부 다른 색깔로 배치)
        // 기존 감정 색상 + 보조 색상들을 조합합니다.
        val bubbleColors = listOf(
            resources.getColor(R.color.emotion_very_happy, null), // 행복
            resources.getColor(R.color.emotion_happy, null),      // 설렘
            resources.getColor(R.color.emotion_neutral, null),    // 걱정
            resources.getColor(R.color.emotion_sad, null),        // 불안
            resources.getColor(R.color.emotion_very_sad, null),   // 슬픔
            resources.getColor(R.color.brand, null)               // 실망 (초록 계열 등 다른 색)
        )

        // 2. 단어별 빈도수 및 색상 매핑
        // 글씨가 잘 보이도록 최소 빈도수를 70 이상으로 높게 설정하고, 간격을 촘촘하게 하여 크기를 키웁니다.
        val dummyWordsWithColors = listOf(
            Triple("행복", 150, bubbleColors[0]),
            Triple("설렘", 130, bubbleColors[1]),
            Triple("걱정", 115, bubbleColors[2]),
            Triple("불안", 100, bubbleColors[3]),
            Triple("슬픔", 85, bubbleColors[4]),
            Triple("실망", 75, bubbleColors[5])
        )

        // 3. 커스텀 뷰의 setWords가 색상까지 지원하도록 설계되어 있다면 아래와 같이 사용합니다.
        // 만약 setWords가 List<Pair<String, Int>>만 받는다면,
        // 뷰 내부 소스에서 순차적으로 bubbleColors를 적용하도록 수정해야 할 수도 있습니다.

        // 여기서는 데이터의 빈도수(Int)를 더 크게 상향 조정하여 버블을 키웁니다.
        val dummyWords = listOf(
            "행복" to 150, // 120 -> 150으로 상향
            "설렘" to 135,
            "걱정" to 120,
            "불안" to 105,
            "슬픔" to 90,  // 글씨가 잘 보이도록 70 -> 90 상향
            "실망" to 80   // 글씨가 잘 보이도록 60 -> 80 상향
        )

        // 4. 차트 렌더링
        wbvCounselingStatisticsWordFrequency.setWords(dummyWords)

        // 참고: 만약 뷰에서 색상 지정을 지원하지 않는다면,
        // 뷰 객체 자체에 배경색 리스트를 전달하는 메소드가 있는지 확인해보세요.
        // 예: wbvCounselingStatisticsWordFrequency.setBubbleColors(bubbleColors)
    }

    private fun initHorizontalBar(layout: HorizontalBarChart, value: Float, label: String, colorRes: Int, maxValue: Float) {
        val entries = listOf(BarEntry(0f, value))
        val dataSet = BarDataSet(entries, label).apply {
            color = resources.getColor(colorRes, null)
            setDrawValues(false) // 막대 위의 숫자 제거
        }
        with(layout) {
            val barData = BarData(dataSet).apply {
                barWidth = 1.0f
            }
            data = barData

            description.isEnabled = false // 차트 오른쪽 하단 라벨(설명 문구)을 제거한다.
            legend.isEnabled = false // 범례(데이터 색상이 무엇을 뜻하는지 설명하는 박스)를 제거한다.
            xAxis.isEnabled = false // X축 라인과 축에 적히는 값(0, 1, 2...)을 모두 숨긴다.

            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = maxValue
            axisLeft.isEnabled = false // Y축 왼쪽 수치 가이드라인을 숨긴다.
            axisRight.isEnabled = false // Y축 오른쪽 수치 가이드라인을 숨긴다.

            setTouchEnabled(false) // 사용자의 터치 인터렉션을 막는다.
            invalidate() // 화면을 다시 그린다.
        }
    }
}