package com.egobook.app.ui.square.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.egobook.app.databinding.DialogMarketingConsentBinding
import com.egobook.app.ui.square.viewmodel.QuestionViewModel

class MarketingConsentDialog : DialogFragment() {
    private var _binding: DialogMarketingConsentBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private val questionViewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogMarketingConsentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMarketingConsentAgree.setOnClickListener {
            questionViewModel.updateMarketingConsent(enabled = true)
        }

        binding.btnMarketingConsentDisagree.setOnClickListener {
            questionViewModel.updateMarketingConsent(enabled = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MarketingConsentDialog"
    }
}
