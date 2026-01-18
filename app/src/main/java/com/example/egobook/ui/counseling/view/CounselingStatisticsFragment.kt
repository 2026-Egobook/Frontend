package com.example.egobook.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.egobook.R
import com.example.egobook.databinding.FragmentCounselingStatisticsBinding
import com.example.egobook.domain.model.EmotionType
import com.example.egobook.ui.counseling.model.StatisticsModel
import com.example.egobook.ui.counseling.viewmodel.StatisticsViewModel
import com.example.egobook.util.UiState
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
import kotlin.collections.toFloatArray
import kotlin.math.abs

@AndroidEntryPoint
class CounselingStatisticsFragment : Fragment(R.layout.fragment_counseling_statistics) {
    private lateinit var binding: FragmentCounselingStatisticsBinding
    private val viewModel: StatisticsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingStatisticsBinding.bind(view)
        fetchData()
        initObservers()
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
                            initTotalCountChart(data)
                            initDailyStackedBarChart(data)
                            initMonthlyTrendLineChart(data)
                            initWordBubbleChart(data)
                        }
                    }
                }
            }
        }
    }

    private fun initTotalCountChart(statisticsInfo: StatisticsModel) = with(binding) {
        val veryGoodTotalCnt = statisticsInfo.emotions[EmotionType.VERY_GOOD]?.totalCnt ?: 0
        val goodTotalCnt = statisticsInfo.emotions[EmotionType.GOOD]?.totalCnt ?: 0
        val normalTotalCnt = statisticsInfo.emotions[EmotionType.NORMAL]?.totalCnt ?: 0
        val badTotalCnt = statisticsInfo.emotions[EmotionType.BAD]?.totalCnt ?: 0
        val veryBadTotalCnt = statisticsInfo.emotions[EmotionType.VERY_BAD]?.totalCnt ?: 0

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

    private fun initDailyStackedBarChart(statisticsInfo: StatisticsModel) = with(binding) {
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

        val entries = (0..6).map { dayIdx ->
            val values = EmotionType.entries.map { type ->
                statisticsInfo.emotions[type]?.months?.sumOf { month ->
                    month.days[dayIdx].totalCnt
                }?.toFloat() ?: 0f
            }.toFloatArray()
            BarEntry(dayIdx.toFloat(), values)
        }

        val dataSet = BarDataSet(entries, "요일별 감정 분포").apply {
            colors = listOf(
                resources.getColor(R.color.emotion_very_happy, null),
                resources.getColor(R.color.emotion_happy, null),
                resources.getColor(R.color.emotion_neutral, null),
                resources.getColor(R.color.emotion_sad, null),
                resources.getColor(R.color.emotion_very_sad, null)
            )
            setDrawValues(false) // 막대 위의 숫자 제거
        }
        with(bcCounselingStatisticsDailyRecord) {
            data = BarData(dataSet).apply {
                barWidth = 0.5f
            }
            xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false) // X축 세로 격자선 제거
            xAxis.setDrawAxisLine(false) // X축 가로선 제거
            xAxis.textColor = resources.getColor(R.color.stacked_bar_chart_text, null)
            xAxis.textSize = 13f
            legend.isEnabled = false
            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setExtraOffsets(0f, 0f, 0f, 10f)
            setTouchEnabled(false)
            invalidate()
        }

        tvCounselingStatisticsDailyRecordPeakPositiveTime.text = getPickTime(statisticsInfo, EmotionType.VERY_GOOD)
        tvCounselingStatisticsDailyRecordPeakNegativeTime.text = getPickTime(statisticsInfo, EmotionType.VERY_BAD)

    }

    private fun getPickTime(statisticsInfo: StatisticsModel, type: EmotionType): String {
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
        var maxCnt = -1
        var result = "기록 없음"
        for(dayIdx in 0..6) {
            for(hourIdx in 0..23) {
                val count = statisticsInfo.emotions[type]?.months?.sumOf { month ->
                    month.days[dayIdx].hours[hourIdx]
                } ?: 0
                if(count > 0 && count > maxCnt) {
                    maxCnt = count
                    result = "${dayLabels[dayIdx]} ${hourIdx}시"
                }
            }
        }
        return result
    }

    /**
     * 월별 평균 감정 점수를 보여주려면 감정당 가중치를 줘야한다.
     */
    private fun initMonthlyTrendLineChart(statisticsInfo: StatisticsModel) = with(binding) {

        val calendar = java.util.Calendar.getInstance()
        val currentMonthIdx = calendar.get(java.util.Calendar.MONTH)
        val lastMonthIdx = (currentMonthIdx + 11)%12

        fun calculateMonthlyAverage(monthIdx: Int): Float {
            var totalScore = 0
            var totalCount = 0

            EmotionType.entries.forEach { type ->
                val count = statisticsInfo.emotions[type]?.months[monthIdx]?.totalCnt ?: 0
                val weight = when(type) {
                    EmotionType.VERY_BAD -> 1
                    EmotionType.BAD -> 2
                    EmotionType.NORMAL -> 3
                    EmotionType.GOOD -> 4
                    EmotionType.VERY_GOOD -> 5
                }
                totalScore += count*weight
                totalCount += count
            }

            return if(totalCount > 0) totalScore.toFloat()/totalCount else 0f
        }

        val currentMonthAvg = calculateMonthlyAverage(monthIdx = currentMonthIdx)
        val lastMonthAvg = calculateMonthlyAverage(monthIdx = lastMonthIdx)
        val diff = currentMonthAvg - lastMonthAvg
        val diffText = if(diff >0) "지난달보다 ${String.format("%.1f", diff)} 상승했어요" else if(diff<0) "지난달보다 ${String.format("%.1f", abs(diff))} 하락했어요" else "지난달과 점수가 동일해요"

        tvCounselingStatisticsAverageLastMonthLabel.text = "${lastMonthIdx+1}월 평균 점수"
        tvCounselingStatisticsAverageLastMonthValue.text = String.format("%.1f", lastMonthAvg)
        tvCounselingStatisticsAverageCurrentMonthLabel.text = "${currentMonthIdx+1}월 평균 점수"
        tvCounselingStatisticsAverageCurrentMonthValue.text = String.format("%.1f", currentMonthAvg)
        tvCounselingStatisticsAverageComparison.text = diffText

        val lastSixMonthIndices = (5 downTo 0).map { i ->
            (currentMonthIdx + 12 - i) % 12
        }

        val monthLabels = lastSixMonthIndices.map { "${it+1}월" }

        val entries = lastSixMonthIndices.mapIndexed { chartIdx, monthIdx ->
            Entry(chartIdx.toFloat(), calculateMonthlyAverage(monthIdx = monthIdx))
        }

        val dataSet = LineDataSet(entries, "평균 감정 점수").apply {
            mode = LineDataSet.Mode.LINEAR
            color = resources.getColor(R.color.brand, null)
            setCircleColor(resources.getColor(R.color.brand, null))
            lineWidth = 2f
            circleRadius = 5f
            setDrawValues(false)
        }
        with(lcCounselingStatisticsTrend) {
            data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // x축 단위 간격을 1로 고정
            axisLeft.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }

    }

    // enum class로 빼기. 1~10 data class
    private fun initWordBubbleChart(statisticsInfo: StatisticsModel) = with(binding) {

        val veryGoodCnt = statisticsInfo.emotions[EmotionType.VERY_GOOD]?.totalCnt ?: 0
        val goodCnt = statisticsInfo.emotions[EmotionType.GOOD]?.totalCnt ?: 0
        val normalCnt = statisticsInfo.emotions[EmotionType.NORMAL]?.totalCnt ?: 0
        val badCnt = statisticsInfo.emotions[EmotionType.BAD]?.totalCnt ?: 0
        val veryBadCnt = statisticsInfo.emotions[EmotionType.VERY_BAD]?.totalCnt ?: 0

        val data = listOf(
            "매우 기쁨" to veryGoodCnt,
            "기쁨" to goodCnt,
            "보통" to normalCnt,
            "슬픔" to badCnt,
            "매우 슬픔" to veryBadCnt
        )
        wbvCounselingStatisticsWordFrequency.setWords(data) // 차트 렌더링
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