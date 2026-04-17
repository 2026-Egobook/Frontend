package com.egobook.app.ui.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.egobook.app.R
import com.egobook.app.databinding.DialogRadarBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.home.repository.UserTendencyRepository
import com.egobook.app.ui.home.RadarView
import com.egobook.app.ui.home.user.Tendency
import com.egobook.app.ui.home.user.TendencyType
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RadarDialog(): DialogFragment() {

    private var _binding: DialogRadarBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    @Inject
    lateinit var userTendencyRepository: UserTendencyRepository

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
            binding.progressBar.visibility = View.VISIBLE
            binding.contentLayout.visibility = View.GONE
            try {
                val data = withContext(Dispatchers.IO) {
                    userTendencyRepository.loadTendencies()
                }
                radarView.setRadarData(data.sortedBy { it.type.order() }.map { it.experiencePoint })
                showTendencyLevel(data)
            } catch (e: Exception) {
                Log.d("error", e.toString())
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
            }
        }
        binding.ivRadarClose.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTendencyLevel(tendencies: List<Tendency>) {
        for(tendency in tendencies) {
            when(tendency.type) {
                TendencyType.EMPATHY -> binding.tvEmpathyLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                TendencyType.SELF_ESTEEM -> binding.tvSelfEsteemLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                TendencyType.DILIGENCE -> binding.tvDiligenceLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                TendencyType.POSITIVE_THINKING -> binding.tvPositiveThinkingLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
                TendencyType.EMOTION_REGULATION -> binding.tvEmotionRegulationLevel.text = "Lv. ${tendency.level.toString().padStart(3, '0')}"
            }
        }
    }
    private fun TendencyType.order(): Int {
        return when(this) {
            TendencyType.EMPATHY -> return 4
            TendencyType.SELF_ESTEEM -> return 3
            TendencyType.DILIGENCE -> 0
            TendencyType.POSITIVE_THINKING -> 2
            TendencyType.EMOTION_REGULATION -> 1
        }
    }
}
