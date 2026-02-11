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
import com.egobook.app.removeScreenBlur

class StoreLeavingDialog: DialogFragment() {
    private var _binding: DialogLeavingStoreBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogLeavingStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: StoreViewModel by activityViewModels()
        binding.btnRemain.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }


        binding.btnLeave.setOnClickListener {
            removeScreenBlur()
            dismiss()
            viewModel.resetEquipItems()
            findNavController().navigate(R.id.action_storeFragment_to_homeFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
