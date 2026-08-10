package com.egobook.app.ui.counseling.view

import android.graphics.Typeface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.BuildConfig
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomStatisticsBinding
import com.egobook.app.domain.model.MonthlyAverage
import com.egobook.app.domain.model.MoodPeakTime
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs
import timber.log.Timber

@AndroidEntryPoint
class EgoRoomStatisticsFragment : Fragment(R.layout.fragment_ego_room_statistics) {
    private lateinit var binding: FragmentEgoRoomStatisticsBinding
    private val viewModel: StatisticsViewModel by activityViewModels()
    private var tooltipPopup: PopupWindow? = null
    private var interstitialAd: InterstitialAd? = null
    private var hasShownInterstitialAd = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomStatisticsBinding.bind(view)
        loadInterstitialAd()
        initViews()
        initObservers()
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        showInterstitialAdIfReady()
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireContext(),
            BuildConfig.ADMOB_INTERSTITIAL_STATISTICS_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Timber.d("통계 전면 광고 로드 성공")
                    if (isResumed) showInterstitialAdIfReady()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Timber.d("통계 전면 광고 로드 실패, $adError")
                }
            }
        )
    }

    private fun showInterstitialAdIfReady() {
        if (hasShownInterstitialAd) return
        val ad = interstitialAd ?: return
        hasShownInterstitialAd = true
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Timber.d("통계 전면 광고 표시 실패, $adError")
            }
        }
        ad.show(requireActivity())
    }

    private fun initViews() {
        initTooltips()
        initNoDataTexts()
    }

    private fun fetchData() {
        viewModel.fetchStatistics()
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statisticsInfo.collect { state ->
                    when (state) {
                        is UiState.Failure -> Unit
                        UiState.Idle -> Unit
                        UiState.Loading -> Unit
                        is UiState.Success<StatisticsModel> -> bindStatistics(state.data)
                    }
                }
            }
        }
    }

    private fun bindStatistics(data: StatisticsModel) = with(binding) {
        bindTotalCountChart(data)
        bindDailyStackedBarChart(data)
        bindMonthlyTrendLineChart(data)
        bindWordBubbleChart(data)
        tvCounselingStatisticsDailyRecordPeakPositiveTime.text =
            formatPeakTime(data.moodPeak.goodMood)
        tvCounselingStatisticsDailyRecordPeakNegativeTime.text =
            formatPeakTime(data.moodPeak.badMood)
    }

    private fun bindTotalCountChart(data: StatisticsModel) = with(binding) {
        val countByLevel = data.totalStats.counts.associate { it.emotionLevel to it.totalCount }
        val veryGoodTotalCnt = countByLevel[5] ?: 0
        val goodTotalCnt = countByLevel[4] ?: 0
        val normalTotalCnt = countByLevel[3] ?: 0
        val badTotalCnt = countByLevel[2] ?: 0
        val veryBadTotalCnt = countByLevel[1] ?: 0
        val maxCount = maxOf(
            data.totalStats.maxCount,
            veryGoodTotalCnt,
            goodTotalCnt,
            normalTotalCnt,
            badTotalCnt,
            veryBadTotalCnt,
            1
        ).toFloat()

        initHorizontalBar(hbcCounselingStatisticsTotalCountVeryHappy, veryGoodTotalCnt.toFloat(), "VeryHappy", R.color.emotion_very_happy, maxCount)
        tvCounselingStatisticsTotalCountVeryHappyCount.text = "${veryGoodTotalCnt}\uD68C"

        initHorizontalBar(hbcCounselingStatisticsTotalCountHappy, goodTotalCnt.toFloat(), "Happy", R.color.emotion_happy, maxCount)
        tvCounselingStatisticsTotalCountHappyCount.text = "${goodTotalCnt}\uD68C"

        initHorizontalBar(hbcCounselingStatisticsTotalCountNeutral, normalTotalCnt.toFloat(), "Neutral", R.color.emotion_neutral, maxCount)
        tvCounselingStatisticsTotalCountNeutralCount.text = "${normalTotalCnt}\uD68C"

        initHorizontalBar(hbcCounselingStatisticsTotalCountSad, badTotalCnt.toFloat(), "Sad", R.color.emotion_sad, maxCount)
        tvCounselingStatisticsTotalCountSadCount.text = "${badTotalCnt}\uD68C"

        initHorizontalBar(hbcCounselingStatisticsTotalCountVerySad, veryBadTotalCnt.toFloat(), "VerySad", R.color.emotion_very_sad, maxCount)
        tvCounselingStatisticsTotalCountVerySadCount.text = "${veryBadTotalCnt}\uD68C"

        val targetChartId = listOf(
            veryGoodTotalCnt to hbcCounselingStatisticsTotalCountVeryHappy.id,
            goodTotalCnt to hbcCounselingStatisticsTotalCountHappy.id,
            normalTotalCnt to hbcCounselingStatisticsTotalCountNeutral.id,
            badTotalCnt to hbcCounselingStatisticsTotalCountSad.id,
            veryBadTotalCnt to hbcCounselingStatisticsTotalCountVerySad.id
        ).maxByOrNull { it.first }
            ?.takeIf { it.first > 0 }
            ?.second
        updateCrownPosition(targetChartId)
    }

    private fun bindDailyStackedBarChart(data: StatisticsModel) = with(binding) {
        val weekData = normalizeWeekdayData(data)
        val dayLabels = weekData.map { formatDayLabel(it.day) }
        val entries = weekData.mapIndexed { index, day ->
            val levelMap = day.levels.associate { it.emotionLevel to it.percentOfMax.toFloat() }
            BarEntry(
                index.toFloat(),
                floatArrayOf(
                    levelMap[1] ?: 0f,
                    levelMap[2] ?: 0f,
                    levelMap[3] ?: 0f,
                    levelMap[4] ?: 0f,
                    levelMap[5] ?: 0f
                )
            )
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
            this.data = BarData(dataSet).apply { barWidth = 0.4f }
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dayLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = resources.getColor(R.color.stacked_bar_chart_text, null)
                textSize = 13f
                granularity = 1f
                yOffset = 12f
            }
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = 100f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)
            setExtraOffsets(0f, 0f, 0f, 15f)
            invalidate()
            post { updateWeekdayCrowns(weekData) }
        }
    }

    private fun bindMonthlyTrendLineChart(data: StatisticsModel) = with(binding) {
        val avgs = data.sixMonthAvgs.takeLast(6).ifEmpty {
            listOf(MonthlyAverage(year = 0, month = 0, avg = 0f))
        }
        val entries = avgs.mapIndexed { index, item -> Entry(index.toFloat(), item.avg) }
        val monthLabels = avgs.map { if (it.month > 0) "${it.month}\uC6D4" else "-" }
        val currentMonthScore = avgs.last().avg
        val lastMonthScore = avgs.getOrNull(avgs.lastIndex - 1)?.avg ?: currentMonthScore
        val diff = currentMonthScore - lastMonthScore

        tvCounselingStatisticsAverageLastMonthLabel.text =
            "${monthLabels.getOrNull(monthLabels.lastIndex - 1) ?: "\uC9C0\uB09C\uB2EC"} \uD3C9\uADE0 \uC810\uC218"
        tvCounselingStatisticsAverageLastMonthValue.text = String.format("%.1f", lastMonthScore)
        tvCounselingStatisticsAverageCurrentMonthLabel.text =
            "${monthLabels.lastOrNull() ?: "\uC774\uBC88\uB2EC"} \uD3C9\uADE0 \uC810\uC218"
        tvCounselingStatisticsAverageCurrentMonthValue.text = String.format("%.1f", currentMonthScore)
        tvCounselingStatisticsAverageComparison.text = buildComparisonText(diff)

        val dataSet = LineDataSet(entries, "평균 점수").apply {
            color = resources.getColor(R.color.brand, null)
            setCircleColor(resources.getColor(R.color.brand, null))
            lineWidth = 2.5f
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
        }

        with(lcCounselingStatisticsTrend) {
            this.data = LineData(dataSet)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                yOffset = 10f
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = maxOf(5f, avgs.maxOfOrNull { it.avg } ?: 5f)
                setLabelCount(5, true)
                setDrawGridLines(true)
                gridColor = resources.getColor(R.color.neutral_stroke, null)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setExtraOffsets(10f, 0f, 20f, 10f)
            invalidate()
        }
    }

    private fun bindWordBubbleChart(data: StatisticsModel) = with(binding) {
        val words = data.wordCloud
            .filter { it.word.isNotBlank() && it.weight > 0 }
            .sortedByDescending { it.weight }
            .take(6)

        val maxWeight = words.maxOfOrNull { it.weight } ?: 0
        val minWeight = words.minOfOrNull { it.weight } ?: 0
        val normalizedWords = words.map { item ->
            val normalized = if (maxWeight == minWeight) {
                8
            } else {
                4 + ((item.weight - minWeight).toFloat() / (maxWeight - minWeight) * 8).toInt()
            }
            item.word to normalized.coerceIn(4, 12)
        }

        wbvCounselingStatisticsWordFrequency.setWords(normalizedWords)
    }

    private fun initHorizontalBar(
        layout: HorizontalBarChart,
        value: Float,
        label: String,
        colorRes: Int,
        maxValue: Float
    ) {
        val entries = listOf(BarEntry(0f, value))
        val dataSet = BarDataSet(entries, label).apply {
            color = resources.getColor(colorRes, null)
            setDrawValues(false)
        }
        with(layout) {
            data = BarData(dataSet).apply { barWidth = 1.0f }
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = maxValue
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun normalizeWeekdayData(data: StatisticsModel) =
        DEFAULT_DAYS.map { day ->
            data.stacked.byWeekday.firstOrNull {
                normalizeDayKey(it.day) == normalizeDayKey(day)
            } ?: com.egobook.app.domain.model.WeekdayStack(day = day, levels = emptyList())
        }

    private fun formatPeakTime(peak: MoodPeakTime?): String {
        return peak?.let { "${formatDayLabel(it.day)} ${it.hour.coerceIn(0, 23)}\uC2DC" } ?: "-"
    }

    private fun formatDayLabel(day: String): String {
        return when (normalizeDayKey(day)) {
            "MONDAY", "MON", "1", "\uC6D4" -> "\uC6D4"
            "TUESDAY", "TUE", "2", "\uD654" -> "\uD654"
            "WEDNESDAY", "WED", "3", "\uC218" -> "\uC218"
            "THURSDAY", "THU", "4", "\uBAA9" -> "\uBAA9"
            "FRIDAY", "FRI", "5", "\uAE08" -> "\uAE08"
            "SATURDAY", "SAT", "6", "\uD1A0" -> "\uD1A0"
            "SUNDAY", "SUN", "7", "\uC77C" -> "\uC77C"
            else -> day.take(1)
        }
    }

    private fun normalizeDayKey(day: String): String = day.trim().uppercase()

    private fun buildComparisonText(diff: Float): SpannableStringBuilder {
        val direction = when {
            diff > 0f -> "\uC0C1\uC2B9"
            diff < 0f -> "\uD558\uB77D"
            else -> "\uC720\uC9C0"
        }
        val boldText = "${String.format("%.1f", abs(diff))} $direction"
        val normalTextStart = "\uC9C0\uB09C\uB2EC \uBCF4\uB2E4 "
        val normalTextEnd = "\uD588\uC5B4\uC694"
        val spannable = SpannableStringBuilder(normalTextStart + boldText + normalTextEnd)
        val start = normalTextStart.length
        val end = start + boldText.length
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun initTooltips() = with(binding) {
        ivCounselingStatisticsTotalCountQuestion.setOnClickListener {
            showTooltip(it, buildTotalCountTooltipText())
        }
        ivCounselingStatisticsDailyRecordQuestion.setOnClickListener {
            showTooltip(it, buildDailyRecordTooltipText())
        }
        ivCounselingStatisticsTrendQuestion.setOnClickListener {
            showTooltip(it, buildTrendTooltipText())
        }
        ivCounselingStatisticsWordFrequencyQuestion.setOnClickListener {
            showTooltip(it, buildWordFrequencyTooltipText())
        }
    }

    private fun initNoDataTexts() = with(binding) {
        val charts = listOf(
            hbcCounselingStatisticsTotalCountVeryHappy,
            hbcCounselingStatisticsTotalCountHappy,
            hbcCounselingStatisticsTotalCountNeutral,
            hbcCounselingStatisticsTotalCountSad,
            hbcCounselingStatisticsTotalCountVerySad,
            bcCounselingStatisticsDailyRecord,
            lcCounselingStatisticsTrend
        )
        charts.forEach { chart ->
            chart.setNoDataText(NO_DATA_TEXT)
            chart.setNoDataTextColor(resources.getColor(R.color.neutral_subtle, null))
        }
    }

    private fun updateCrownPosition(targetChartId: Int?) = with(binding) {
        ivCounselingStatisticsTotalCountCrown.isVisible = targetChartId != null
        if (targetChartId == null) return@with

        ConstraintSet().apply {
            clone(clCounselingStatisticsTotalCount)
            clear(ivCounselingStatisticsTotalCountCrown.id, ConstraintSet.TOP)
            clear(ivCounselingStatisticsTotalCountCrown.id, ConstraintSet.BOTTOM)
            clear(ivCounselingStatisticsTotalCountCrown.id, ConstraintSet.END)
            connect(
                ivCounselingStatisticsTotalCountCrown.id,
                ConstraintSet.TOP,
                targetChartId,
                ConstraintSet.TOP
            )
            connect(
                ivCounselingStatisticsTotalCountCrown.id,
                ConstraintSet.BOTTOM,
                targetChartId,
                ConstraintSet.BOTTOM
            )
            connect(
                ivCounselingStatisticsTotalCountCrown.id,
                ConstraintSet.END,
                targetChartId,
                ConstraintSet.END,
                (17 * resources.displayMetrics.density).toInt()
            )
            applyTo(clCounselingStatisticsTotalCount)
        }
    }

    private fun buildTotalCountTooltipText(): SpannableStringBuilder {
        val title = "\uC804\uCCB4 \uAC10\uC815 \uAE30\uB85D \uD69F\uC218"
        val body = "\n\n1\uB144 \uB3D9\uC548 \uC5B4\uB5A4 \uAC10\uC815\uC744 \uB9CE\uC774 \uB290\uAF08\uB098\uC694?\n" +
            "\uAC10\uC815 \uC77C\uAE30\uC5D0 \uAE30\uB85D\uD55C 1\uC810~5\uC810\uC758 \uAC10\uC815\uC744 \uC885\uB958\uBCC4\uB85C \uBAA8\uC544\uC11C \uBCF4\uC5EC\uC918\uC694. " +
            "\uAC00\uC7A5 \uB9CE\uC774 \uAE30\uB85D\uD55C \uAC10\uC815\uC774 \uAE30\uC900(100%)\uC774 \uB418\uACE0, " +
            "\uB098\uBA38\uC9C0 \uAC10\uC815\uC740 \uADF8 \uBE44\uC728\uB9CC\uD07C \uB9C9\uB300 \uAE38\uC774\uB85C \uD45C\uC2DC\uB3FC\uC694.\n\n" +
            "\uAC00\uC7A5 \uB9CE\uC774 \uAE30\uB85D\uD55C \uAC10\uC815\uC5D0 \uC655\uAD00\uC774 \uBD99\uC5B4\uC694.\n" +
            "\uAE30\uB85D\uC744 \uC218\uC815\uD558\uAC70\uB098 \uC0AD\uC81C\uD558\uBA74 \uD1B5\uACC4\uC5D0 \uBC14\uB85C \uBC18\uC601\uB3FC\uC694."
        return SpannableStringBuilder(title + body).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun showTooltip(anchor: View, content: CharSequence) {
        tooltipPopup?.dismiss()

        val density = resources.displayMetrics.density
        val popupText = TextView(requireContext()).apply {
            text = content
            setTextColor(Color.WHITE)
            textSize = 14f
            setLineSpacing(2 * density, 1.08f)
            typeface = resources.getFont(R.font.arita_medium)
            letterSpacing = -0.02f
            setBackgroundResource(R.drawable.bg_tooltip)
            setPadding(
                (12 * density).toInt(),
                (12 * density).toInt(),
                (12 * density).toInt(),
                (12 * density).toInt()
            )
        }
        val scrollView = object : ScrollView(requireContext()) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                val maxHeight = (240 * density).toInt()
                val cappedHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
                super.onMeasure(widthMeasureSpec, cappedHeightSpec)
            }
        }.apply {
            isFillViewport = false
            addView(
                popupText,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        tooltipPopup = PopupWindow(
            scrollView,
            (300 * density).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 8 * density
            showAsDropDown(anchor, (-80 * density).toInt(), (4 * density).toInt())
        }
    }

    private fun buildDailyRecordTooltipText(): SpannableStringBuilder {
        val title = "\uC694\uC77C\uBCC4 \uAC10\uC815 \uAE30\uB85D"
        val body = "\n\n\uC5B4\uB5A4 \uC694\uC77C\uC5D0 \uAE30\uBD84\uC774 \uC88B\uACE0 \uB098\uBE74\uB098\uC694?\n" +
            "\uC694\uC77C\uBCC4\uB85C \uAC10\uC815\uC774 \uC5B4\uB5BB\uAC8C \uBD84\uD3EC\uB418\uC5B4 \uC788\uB294\uC9C0 \uC313\uC778 \uB9C9\uB300 \uADF8\uB798\uD504\uB85C \uBCF4\uC5EC\uC918\uC694. " +
            "\uB9C9\uB300 \uC548\uC5D0\uC11C \uAC01 \uAC10\uC815\uC774 \uCC28\uC9C0\uD558\uB294 \uBE44\uC728\uC744 \uC0C9\uAE54\uB85C \uB098\uB220\uC11C \uD45C\uC2DC\uD574\uC694.\n\n" +
            "\uB9C9\uB300 \uB9E8 \uC704\uAC00 5\uC810 (\uAC00\uC7A5 \uAE0D\uC815),\n" +
            "\uB9E8 \uC544\uB798\uAC00 1\uC810 (\uAC00\uC7A5 \uBD80\uC815)\uC774\uC5D0\uC694.\n" +
            "\uAC01 \uC694\uC77C\uC5D0\uC11C \uAC00\uC7A5 \uB9CE\uC740 \uBE44\uC728\uC744 \uCC28\uC9C0\uD55C \uAC10\uC815\uC5D0 \uC655\uAD00\uC774 \uBD99\uC5B4\uC694.\n" +
            "\uC8FC\uB85C \uAE30\uBD84\uC774 \uC88B\uC558\uB358 \uC694\uC77C & \uC2DC\uAC04\uB300\uC640 \uB098\uBE74\uB358 \uC694\uC77C & \uC2DC\uAC04\uB300\uB3C4 \uD568\uAED8 \uC54C\uB824\uC918\uC694."
        return buildTooltipText(title, body)
    }

    private fun buildTrendTooltipText(): SpannableStringBuilder {
        val title = "6\uAC1C\uC6D4 \uD3C9\uADE0 \uAC10\uC815 \uC810\uC218"
        val body = "\n\n\uCD5C\uADFC 6\uAC1C\uC6D4\uC758 \uC6D4\uBCC4 \uD3C9\uADE0 \uAC10\uC815 \uC810\uC218\uB97C \uC120 \uADF8\uB798\uD504\uB85C \uBCF4\uC5EC\uC918\uC694. " +
            "\uAE30\uBD84\uC758 \uD750\uB984\uC744 \uD55C\uB208\uC5D0 \uD30C\uC545\uD560 \uC218 \uC788\uC5B4\uC694.\n\n" +
            "\uADF8\uB798\uD504 \uB9E8 \uC704\uAC00 5\uC810, \uB9E8 \uC544\uB798\uAC00 1\uC810\uC774\uC5D0\uC694.\n" +
            "\uADF8\uB2EC\uC5D0 \uC77C\uAE30\uB97C \uC4F0\uC9C0 \uC54A\uC558\uB2E4\uBA74 0\uC810\uC73C\uB85C \uD45C\uC2DC\uB3FC\uC694. (\uADF8\uB798\uD504 \uCD5C\uD558\uB2E8)\n" +
            "\uC9C0\uB09C\uB2EC\uACFC \uC774\uBC88\uB2EC \uD3C9\uADE0 \uC810\uC218\uB97C \uBE44\uAD50\uD574\uC11C \uC5BC\uB9C8\uB098 \uB2EC\uB77C\uC84C\uB294\uC9C0 \uC54C\uB824\uC918\uC694.\n" +
            "\uB9E4\uB2EC \uC0C8 \uB370\uC774\uD130\uAC00 \uCD94\uAC00\uB418\uACE0, 6\uAC1C\uC6D4 \uC774\uC804 \uB370\uC774\uD130\uB294 \uC790\uB3D9\uC73C\uB85C \uC0AC\uB77C\uC838\uC694."
        return buildTooltipText(title, body)
    }

    private fun buildWordFrequencyTooltipText(): SpannableStringBuilder {
        val title = "\uC790\uC8FC \uC4F4 \uB2E8\uC5B4"
        val body = "\n\n\uC77C\uAE30\uC5D0 \uC790\uC8FC \uB4F1\uC7A5\uD55C \uB2E8\uC5B4\uB97C \uD06C\uAE30\uB85C \uD45C\uD604\uD574\uC694. " +
            "\uB9CE\uC774 \uC4F4 \uB2E8\uC5B4\uC77C\uC218\uB85D \uAE00\uC790\uAC00 \uD06C\uAC8C \uD45C\uC2DC\uB3FC\uC694. " +
            "\uB0B4\uAC00 \uC5B4\uB5A4 \uC8FC\uC81C\uB97C \uC790\uC8FC \uC0DD\uAC01\uD558\uB294\uC9C0 \uB3CC\uC544\uBCFC \uC218 \uC788\uC5B4\uC694.\n\n" +
            "\uAE00\uC790\uAC00 \uD074\uC218\uB85D \uB354 \uC790\uC8FC \uC4F4 \uB2E8\uC5B4\uC608\uC694.\n" +
            "\uCD5C\uADFC 6\uAC1C\uC6D4 \uCE58 \uC77C\uAE30\uC5D0\uC11C \uC790\uB3D9\uC73C\uB85C \uCD94\uCD9C\uD574\uC694."
        return buildTooltipText(title, body)
    }

    private fun buildTooltipText(title: String, body: String): SpannableStringBuilder {
        return SpannableStringBuilder(title + body).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun updateWeekdayCrowns(weekData: List<com.egobook.app.domain.model.WeekdayStack>) = with(binding) {
        val chart = bcCounselingStatisticsDailyRecord
        val crownSize = (15 * resources.displayMetrics.density).toInt()
        val existingCrowns = (0 until chart.childCount)
            .map { chart.getChildAt(it) }
            .filter { it.tag == WEEKDAY_CROWN_TAG }
        existingCrowns.forEach { chart.removeView(it) }

        val contentRect = chart.viewPortHandler.contentRect
        if (contentRect.width() <= 0f || contentRect.height() <= 0f) return@with

        weekData.forEachIndexed { index, day ->
            val levels = day.levels.filter { it.percentOfMax > 0 }
            val winner = levels.maxByOrNull { it.percentOfMax } ?: return@forEachIndexed
            val lowerSum = levels
                .filter { it.emotionLevel < winner.emotionLevel }
                .sumOf { it.percentOfMax }
            val centerPercent = (lowerSum + winner.percentOfMax / 2f).coerceIn(0f, 100f)
            val x = contentRect.left + contentRect.width() * ((index + 0.5f) / DEFAULT_DAYS.size) - crownSize / 2f
            val y = contentRect.bottom - contentRect.height() * (centerPercent / 100f) - crownSize / 2f

            ImageView(requireContext()).apply {
                tag = WEEKDAY_CROWN_TAG
                setImageResource(R.drawable.ic_crown)
                layoutParams = ViewGroup.LayoutParams(crownSize, crownSize)
                translationX = x
                translationY = y
                chart.addView(this)
            }
        }
    }

    companion object {
        private const val NO_DATA_TEXT = "\uC544\uC9C1 \uBAA8\uC778 \uB370\uC774\uD130\uAC00 \uC5C6\uC5B4\uC694"
        private const val WEEKDAY_CROWN_TAG = "weekday_crown"
        private val DEFAULT_DAYS = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    }
}
