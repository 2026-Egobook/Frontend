package com.egobook.app.ui.shop

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.DialogLeavingStoreBinding
import com.egobook.app.databinding.DialogPurchasingItemBinding
import com.egobook.app.removeScreenBlur

class StorePurchasingItemDialog: DialogFragment() {
    private var _binding: DialogPurchasingItemBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogPurchasingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: StoreViewModel by activityViewModels()

        val item = viewModel.loadPurchaseItem()

        binding.tvPrice.text = checkNotNull(item).price.value.toString()

        binding.btnBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }


        binding.btnBuy.setOnClickListener {
            if (item != null) {
                viewModel.purchaseItem(item)
            }
            removeScreenBlur()
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
