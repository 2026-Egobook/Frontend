package com.egobook.app.ui.home.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogRadarBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.home.RadarView
import com.egobook.app.ui.home.HomeViewModel
import com.egobook.app.ui.home.user.Tendency
import com.egobook.app.ui.home.user.TendencyType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RadarDialog : DialogFragment() {

    private var _binding: DialogRadarBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    private val viewModel: HomeViewModel by activityViewModels()
    private var helpPopup: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogRadarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radarView: RadarView = view.findViewById(R.id.custom_radar_view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tendencies.collect { data ->
                        if (data.isNotEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            binding.contentLayout.visibility = View.VISIBLE
                            radarView.setRadarData(data.sortedBy { it.type.order() }.map { it.experiencePoint })
                            showTendencyLevel(data)
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        if (isLoading && viewModel.tendencies.value.isEmpty()) {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        binding.ivRadarClose.setOnClickListener {
            helpPopup?.dismiss()
            removeScreenBlur()
            dismiss()
        }

        binding.ivRadarHelp.setOnClickListener {
            showHelpPopup()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        helpPopup?.dismiss()
        helpPopup = null
        _binding = null
    }

    private fun showTendencyLevel(tendencies: List<Tendency>) {
        for (tendency in tendencies) {
            when (tendency.type) {
                TendencyType.EMPATHY -> {
                    binding.tvEmpathyLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                }
                TendencyType.SELF_ESTEEM -> {
                    binding.tvSelfEsteemLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                }
                TendencyType.DILIGENCE -> {
                    binding.tvDiligenceLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                }
                TendencyType.POSITIVE_THINKING -> {
                    binding.tvPositiveThinkingLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                }
                TendencyType.EMOTION_REGULATION -> {
                    binding.tvEmotionRegulationLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                }
            }
        }
    }

    private fun TendencyType.order(): Int {
        return when (this) {
            TendencyType.EMPATHY -> 4
            TendencyType.SELF_ESTEEM -> 3
            TendencyType.DILIGENCE -> 0
            TendencyType.POSITIVE_THINKING -> 2
            TendencyType.EMOTION_REGULATION -> 1
        }
    }

    private fun showHelpPopup() {
        helpPopup?.dismiss()

        val density = resources.displayMetrics.density
        val popupText = TextView(requireContext()).apply {
            text = buildHelpPopupText()
            setTextColor(Color.WHITE)
            textSize = 14f
            setLineSpacing(2 * density, 1.08f)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.arita_medium)
            setPadding(
                (20 * density).toInt(),
                (16 * density).toInt(),
                (20 * density).toInt(),
                (16 * density).toInt()
            )
        }

        val scrollView = object : ScrollView(requireContext()) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                val maxHeight = (208 * resources.displayMetrics.density).toInt()
                val cappedHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
                super.onMeasure(widthMeasureSpec, cappedHeightSpec)
            }
        }.apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            setBackgroundResource(R.drawable.bg_tooltip)
            clipToOutline = true
            addView(
                popupText,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        val horizontalInset = (16 * density).toInt()
        val popupWidth = (binding.root.width - (horizontalInset * 2)).coerceAtLeast(1)
        scrollView.measure(
            View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupHeight = scrollView.measuredHeight
        val spacing = (8 * density).toInt()
        val popupX = binding.ivRadarHelp.width - popupWidth
        val popupY = -(popupHeight + binding.ivRadarHelp.height + spacing)

        helpPopup = PopupWindow(
            scrollView,
            popupWidth,
            popupHeight,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 8 * density
            showAsDropDown(
                binding.ivRadarHelp,
                popupX,
                popupY
            )
        }
    }

    private fun buildHelpPopupText(): CharSequence {
        val content = RadarHelpContent.buildText()
        return SpannableStringBuilder(content).apply {
            listOf(
                "공감성 (하루 한 번)",
                "자존감",
                "감정조절 (하루 한 번)",
                "긍정사고 (하루 한 번)",
                "성실함"
            ).forEach { title ->
                val start = content.indexOf(title)
                if (start >= 0) {
                    setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        start + title.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }
}
