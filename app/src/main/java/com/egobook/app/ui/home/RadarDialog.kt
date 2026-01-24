package com.egobook.app.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogRadarBinding
import com.egobook.app.removeScreenBlur

class RadarDialog() : DialogFragment() {

    private var _binding: DialogRadarBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

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
        radarView.setRadarData(listOf(4, 5, 2, 1, 3))
        binding.ivRadarClose.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
