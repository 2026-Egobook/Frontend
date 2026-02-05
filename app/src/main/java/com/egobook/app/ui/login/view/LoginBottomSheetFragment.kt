package com.egobook.app.ui.login.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egobook.app.databinding.FragmentLoginBottomSheetBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.onboarding.view.OnboardingActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLoginBottomSheetBinding? = null
    private val binding get() = _binding!!

    // 로그인 확인 콜백 인터페이스
    interface OnLoginConfirmListener {
        fun onLoginConfirmed()
    }

    private var loginConfirmListener: OnLoginConfirmListener? = null

    fun setOnLoginConfirmListener(listener: OnLoginConfirmListener) {
        loginConfirmListener = listener
    }

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

        binding.btnBottomGoogleLogin.setOnClickListener {
            // 로그인 확인 콜백 호출
            loginConfirmListener?.onLoginConfirmed()
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
