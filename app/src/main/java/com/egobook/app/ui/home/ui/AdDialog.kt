package com.egobook.app.ui.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.MainActivity
import com.egobook.app.databinding.DialogAdBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class AdDialog() : DialogFragment() {

    private var _binding: DialogAdBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private val viewModel: HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString(ARG_USER_ID) ?: "사용자가 없습니다"

        viewModel.loadCurrentAdInfo()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.adState.collect { adState ->
                        if (adState.isAvailable) {
                            binding.btnWatch.isEnabled = true
                        } else {
                            binding.btnWatch.isEnabled = false
                        }
                        binding.btnWatch.text = "광고보기 ${adState.currentViewCount}/${adState.maxLimit}"
                        binding.tvAdReward.text = adState.rewardPerAd.toString()
                    }
                }

                launch {
                    viewModel.isLoadingAdInfo.collect { isLoading ->
                        if (isLoading) {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.contentLayout.visibility = View.GONE
                        } else {
                            binding.pbLoading.visibility = View.GONE
                            binding.contentLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }

        binding.btnWatch.setOnClickListener {
            (activity as MainActivity).showAd(userId) {
                viewModel.watchAd()
            }
            removeScreenBlur()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"

        fun newInstance(userId: String): AdDialog {
            val args = Bundle().apply {
                putString(ARG_USER_ID, userId)
            }
            return AdDialog().apply {
                arguments = args
            }
        }
    }
}
