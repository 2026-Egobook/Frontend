package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.databinding.FragmentAccountBottomSheetBinding
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AccountBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAccountBottomSheetBinding? = null
    private val binding get() = _binding!!

    //부모 프래그먼트의 뷰모델 공유
    private val viewModel: AccountViewModel by viewModels({ requireParentFragment() })


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
        observeLinkState()
    }

    private fun observeLinkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 연동 상태 및 이메일 관찰
                launch {
                    viewModel.linkState.collect { state ->
                        val isLinked = state is UiState.Success
                        if (isLinked) {
                            binding.tvAccountEmail.apply {
                                visibility = View.VISIBLE
                                text = viewModel.userEmail.firstOrNull() ?: ""
                            }
                            binding.btnBottomGoogleLogin.apply {
                                // 이메일이 있으면 표시, 없으면 기본 메시지
                                text = "Google계정으로 연동되었습니다"
                                isEnabled = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setClickListener() {
        binding.btnBottomGoogleLogin.setOnClickListener {
            // linkState가 Success가 아닐 때만 클릭 가능
            if (viewModel.linkState.value !is UiState.Success) {
                linkConfirmListener?.onLinkConfirmed()
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