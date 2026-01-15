package com.example.egobook_frontent.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingStatisticsBinding
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

@AndroidEntryPoint
class CounselingStatisticsFragment : Fragment(R.layout.fragment_counseling_statistics) {
    private lateinit var binding: FragmentCounselingStatisticsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingStatisticsBinding.bind(view)
        initTotalCountChart()
        initDailyStackedBarChart()
        initMonthlyTrendLineChart()
        initWordBubbleChart()
    }

    private fun initTotalCountChart() = with(binding) {
        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountVeryHappy, value = 20f, label = "VeryHappy", colorRes = R.color.emotion_very_happy)
        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountHappy, value = 60f, label = "Happy", colorRes = R.color.emotion_happy)
        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountNeutral, value = 100f, label = "Neutral", colorRes = R.color.emotion_neutral)
        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountSad, value = 140f, label = "Sad", colorRes = R.color.emotion_sad)
        initHorizontalBar(layout = hbcCounselingStatisticsTotalCountVerySad, value = 180f, label = "VerySad", colorRes = R.color.emotion_very_sad)
    }

    private fun initDailyStackedBarChart() = with(binding) {
        val days = listOf("월", "화", "수", "목", "금", "토", "일")
        val entries = listOf(
            BarEntry(0f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(1f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(2f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(3f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(4f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(5f, floatArrayOf(2f, 2f, 2f, 2f, 2f)),
            BarEntry(6f, floatArrayOf(2f, 2f, 2f, 2f, 2f))
        )
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
            data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(days)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false) // X축 세로 격자선 제거
            xAxis.setDrawAxisLine(false) // X축 가로선 제거
            legend.isEnabled = false
            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun initMonthlyTrendLineChart() = with(binding) {
        val months = listOf("6월", "7월", "8월", "9월", "10월", "11월")
        val entries = listOf(
            Entry(0f, 2.5f),
            Entry(1f, 3.5f),
            Entry(2f, 6.5f),
            Entry(3f, 5.3f),
            Entry(4f, 6.8f),
            Entry(5f, 7.2f)
        )
        val dataSet = LineDataSet(entries, "평균 감정 점수").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = resources.getColor(R.color.brand, null)
            setCircleColor(resources.getColor(R.color.brand, null))
            lineWidth = 2f
            circleRadius = 5f
            setDrawValues(false)
        }
        with(lcCounselingStatisticsTrend) {
            data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(months)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun initWordBubbleChart() = with(binding) {
        val data = listOf(
            "매우 기쁨" to 10,
            "기쁨" to 8,
            "보통" to 7,
            "슬픔" to 5,
            "매우 슬픔" to 3
        )
        wbvCounselingStatisticsWordFrequency.setWords(data) // 차트 렌더링
    }

    private fun initHorizontalBar(layout: HorizontalBarChart, value: Float, label: String, colorRes: Int) {
        val entries = listOf(BarEntry(0f, value))
        val dataSet = BarDataSet(entries, label).apply {
            color = resources.getColor(colorRes, null)
        }
        with(layout) {
            data = BarData(dataSet)
            description.isEnabled = false // 차트 오른쪽 하단 라벨(설명 문구)을 제거한다.
            legend.isEnabled = false // 범례(데이터 색상이 무엇을 뜻하는지 설명하는 박스)를 제거한다.
            xAxis.isEnabled = false // X축 라인과 축에 적히는 값(0, 1, 2...)을 모두 숨긴다.
            axisLeft.isEnabled = false // Y축 왼쪽 수치 가이드라인을 숨긴다.
            axisRight.isEnabled = false // Y축 오른쪽 수치 가이드라인을 숨긴다.
            setTouchEnabled(false) // 사용자의 터치 인터렉션을 막는다.
            invalidate() // 화면을 다시 그린다.
        }
    }
}