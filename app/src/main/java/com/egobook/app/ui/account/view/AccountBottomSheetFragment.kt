package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egobook.app.databinding.FragmentAccountBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAccountBottomSheetBinding? = null
    private val binding get() = _binding!!

    var isLinked: Boolean = false  // 연동 여부 상태

    //연동 확인 콜백 인터페이스
    interface OnLinkConfirmListener {
        fun onLinkConfirmed()
    }

    private var linkConfirmListener: OnLinkConfirmListener? = null

    fun setOnLinkConfirmListener(listener: OnLinkConfirmListener) {
        linkConfirmListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnBottomGoogleLogin.apply {
            if (isLinked) {
                text = "Google계정으로 연동되었습니다"
                isEnabled = false
            }

            setOnClickListener {
                if (!isLinked) {
                    linkConfirmListener?.onLinkConfirmed()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? AccountFragment)?.clearBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AccountBottomSheet"
    }
}