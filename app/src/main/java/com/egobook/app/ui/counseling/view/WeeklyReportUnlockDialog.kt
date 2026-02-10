package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.DialogWeeklyReportUnlockBinding
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.ui.counseling.viewmodel.WeeklyReportViewModel
import com.egobook.app.ui.home.user.User
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class WeeklyReportUnlockDialog(private val startDate: String): DialogFragment(R.layout.dialog_weekly_report_unlock) {
    private lateinit var binding: DialogWeeklyReportUnlockBinding
    private val viewModel: WeeklyReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWeeklyReportUnlockBinding.bind(view)
        initListeners()
        initObservers()
    }

    private fun initListeners() = with(binding) {
        btnWeeklyReportUnlockUseInk.setOnClickListener {
            viewModel.getUserInfo()
        }
        btnWeeklyReportUnlockWatchAdd.setOnClickListener {
            // TODO: 에드몹 연결 + 주간 리포트 열기
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<User> -> {
                                val userInfo = state.data
                                val currentInk = userInfo.ink.value
                                if (currentInk >= INK_PRICE) {
                                    viewModel.unlockWeeklyReport(startDate = startDate, unlockType = WeeklyReportUnlockType.INK)
                                } else {
                                    Toast.makeText(context, "현재 잉크가 부족합니다!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.unlockWeeklyReportResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "주간 보고서 잠금이 해제 되었습니다!", Toast.LENGTH_SHORT).show()
                                val action = EgoRoomFragmentDirections.actionMenuEgoRoomToCounselingWeeklyReportDetailFragment(startDate = startDate)
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "WeeklyReportUnlockDialog"
        const val INK_PRICE = 10
    }
}