package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.databinding.FragmentAccountDeleteReasonDialogBinding
import com.egobook.app.domain.model.account.WithdrawReasonType
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * 탈퇴 이유 선택 다이얼로그.
 *
 * [AccountDeleteDialog1Fragment]에서 탈퇴를 확정한 뒤 노출되며,
 * 사유 저장 → 회원 탈퇴가 끝나면 [AccountDeleteDialog2Fragment]로 넘어간다.
 */
class AccountDeleteReasonDialogFragment : DialogFragment() {

    private var _binding: FragmentAccountDeleteReasonDialogBinding? = null
    private val binding get() = _binding!!

    //부모 프래그먼트의 뷰모델 공유
    private val viewModel: AccountViewModel by viewModels({ requireParentFragment() })

    private val reasonViews: List<Pair<WithdrawReasonType, TextView>>
        get() = listOf(
            WithdrawReasonType.NOT_USED_OFTEN to binding.tvReasonNotUsedOften,
            WithdrawReasonType.LACK_OF_CONTENT to binding.tvReasonLackOfContent,
            WithdrawReasonType.INCONVENIENT_UI to binding.tvReasonInconvenientUi,
            WithdrawReasonType.DIFFICULT_TO_COLLECT_INK to binding.tvReasonDifficultToCollectInk,
            WithdrawReasonType.OTHER to binding.tvReasonOther,
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentAccountDeleteReasonDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        setTextChangeListener()
        observeSelectedReason()
        observeDeleteAccountState()
        observeWithdrawToastEvent()
    }

    private fun setClickListener() {
        reasonViews.forEach { (reason, textView) ->
            textView.setOnClickListener { viewModel.selectWithdrawReason(reason) }
        }

        binding.btnBack.setOnClickListener {
            viewModel.resetWithdrawReason()
            removeScreenBlur()
            dismiss()
        }

        binding.btnRealDelete.setOnClickListener {
            viewModel.deleteAccount()
        }
    }

    private fun setTextChangeListener() {
        binding.etOtherReason.doAfterTextChanged { editable ->
            viewModel.updateWithdrawReasonText(editable?.toString().orEmpty())
        }
    }

    private fun observeSelectedReason() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedWithdrawReason.collect { selected ->
                    renderSelection(selected)
                }
            }
        }
    }

    private fun renderSelection(selected: WithdrawReasonType?) {
        reasonViews.forEach { (reason, textView) ->
            textView.isSelected = reason == selected
        }

        // 기타를 고르면 항목 자리에 상세 사유 입력 영역을 대신 노출한다
        val isOther = selected == WithdrawReasonType.OTHER
        binding.tvReasonOther.visibility = if (isOther) View.GONE else View.VISIBLE
        binding.dividerOther.visibility = if (isOther) View.GONE else View.VISIBLE
        binding.layoutOtherInput.visibility = if (isOther) View.VISIBLE else View.GONE

        if (!isOther && binding.etOtherReason.text.isNotEmpty()) {
            binding.etOtherReason.setText("")
        }
    }

    private fun observeDeleteAccountState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 사유를 다 고르고 탈퇴 요청 중이 아닐 때만 탈퇴하기를 누를 수 있다
                combine(
                    viewModel.isWithdrawReasonValid,
                    viewModel.deleteAccountState
                ) { isValid, state ->
                    isValid && state !is UiState.Loading
                }.collect { isEnabled ->
                    binding.btnRealDelete.isEnabled = isEnabled
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteAccountState.collect { state ->
                    if (state is UiState.Success) {
                        navigateToDeleteDialog2()
                    }
                }
            }
        }
    }

    private fun observeWithdrawToastEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.withdrawToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToDeleteDialog2() {
        val accountDeleteDialog2Fragment = AccountDeleteDialog2Fragment()
        accountDeleteDialog2Fragment.isCancelable = true
        accountDeleteDialog2Fragment.show(parentFragmentManager, "AccountDeleteDialog2Fragment")
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.resetWithdrawReason()
        removeScreenBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
