package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.DialogArrivedPendingLetterPopupBinding
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterItemModel
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ArrivedPendingLetterPopupDialog(private val letterInfo: ArrivedPendingLetterItemModel): DialogFragment(R.layout.dialog_arrived_pending_letter_popup) {
    private lateinit var binding: DialogArrivedPendingLetterPopupBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogArrivedPendingLetterPopupBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        tvArrivedPendingLetterTitle.text = "낯선 고북이의\n편지가 도착했어요" // TODO: 친구인지 익명인지 구분 필요
        tvArrivedPendingLetterReplyDeadline.text = getRemainingTime(replyDeadlineAt = letterInfo.replyDeadlineAt)
    }

    private fun initListeners() = with(binding) {
        btnArrivedPendingLetterReplyLater.setOnClickListener {
            // TODO: 로직 작성하기 (상태 변경, API 연동 등)
        }
        btnArrivedPendingLetterConfirm.setOnClickListener {
            val dialog = ArrivedPendingLetterDialog(letterInfo = letterInfo).apply { isCancelable = false }
            dialog.show(parentFragmentManager, ArrivedPendingLetterDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
            dismiss()
        }
    }

    /**
     * 2026-01-05T10:00:00+09:00 → 표준 ISO 형식
     * +09:00 = 타임존 오프셋 정보
     * isNegative: 현재 시간이 기한을 지났을 경우
     * ★ 문서 보충 필요 ★
     */
    private fun getRemainingTime(replyDeadlineAt: String): String {
        val deadline: OffsetDateTime = OffsetDateTime.parse(replyDeadlineAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val now: OffsetDateTime = OffsetDateTime.now(ZoneId.systemDefault())
        val duration: Duration = Duration.between(now, deadline)
        return when {
            duration.isNegative || duration.isZero -> "답장 유효기한 만료"
            duration.toDays() > 0 -> "${duration.toDays()}일 남음"
            duration.toHours() > 0 -> "${duration.toHours()}시간 남음"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}분 남음"
            else -> "잠시 후 만료"
        }
    }

    companion object {
        const val TAG = "ArrivedPendingLetterPopupDialog"
    }
}