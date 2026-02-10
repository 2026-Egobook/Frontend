package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.egobook.app.R
import com.egobook.app.databinding.DialogWeeklyReportUnlockBinding

class WeeklyReportUnlockDialog: DialogFragment(R.layout.dialog_weekly_report_unlock) {
    private lateinit var binding: DialogWeeklyReportUnlockBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWeeklyReportUnlockBinding.bind(view)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnWeeklyReportUnlockUseInk.setOnClickListener {
            // TODO: 잉크 사용 + 주간 리포트 열기
        }
        btnWeeklyReportUnlockWatchAdd.setOnClickListener {
            // TODO: 에드몹 연결 + 주간 리포트 열기
        }
    }

    companion object {
        const val TAG = "WeeklyReportUnlockDialog"
    }
}