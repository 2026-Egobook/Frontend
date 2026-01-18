package com.example.egobook_frontent.ui.login.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.egobook_frontent.MainActivity
import com.example.egobook_frontent.databinding.FragmentLoginBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLoginBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //임시 구현
        binding.btnBottomGoogleLogin.setOnClickListener {
            // 1. MainActivity로 가는 Intent 생성
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)

            // 2. 현재 부모 Activity(LoginActivity)와 바텀시트를 모두 종료
            requireActivity().finish()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as? LoginActivity)?.clearBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LoginBottomSheet"
    }
}
