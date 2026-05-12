package com.egobook.app.ui.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogPsychologyBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.home.PsychologyViewModel
import kotlinx.coroutines.launch
import kotlin.math.max

class PsychologyDialog() : DialogFragment() {

    private var _binding: DialogPsychologyBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    private var ink: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(0)
        }
        _binding = DialogPsychologyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: PsychologyViewModel by activityViewModels()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dailyPhycologyDto.collect { dailyPhycologyDto ->
                        binding.tvPsychologyContent.text = dailyPhycologyDto.knowledge.content
                        binding.tvPsychologySource.text = dailyPhycologyDto.knowledge.source
                        binding.tvPsychologyDate.text = dailyPhycologyDto.date
                        ink = max(ink, dailyPhycologyDto.reward?.inkGranted ?: 0)
                        binding.btnReward.text = "잉크 ${ink}개 획득!"
                        if (dailyPhycologyDto.isBookmarked) {
                            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_clicked)
                        } else {
                            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_unclicked)
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        if (isLoading) {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.vLoadingOverlay.visibility = View.VISIBLE
                        } else {
                            binding.pbLoading.visibility = View.GONE
                            binding.vLoadingOverlay.visibility = View.GONE
                        }
                    }
                }
            }
        }

        binding.ivBookmark.setOnClickListener {
            if (viewModel.dailyPhycologyDto.value.isBookmarked) {
                viewModel.deletePsychology(viewModel.dailyPhycologyDto.value.knowledge.knowledgeId)
            } else {
                viewModel.savePsychology(viewModel.dailyPhycologyDto.value.knowledge.knowledgeId)
            }
        }

        binding.btnReward.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        setFragmentResult("psychology_key", bundleOf("isUpdated" to true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
