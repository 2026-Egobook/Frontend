package com.egobook.app.ui.diary.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.egobook.app.databinding.FragmentMonthDialogBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.R
import com.google.android.material.button.MaterialButton
import java.time.YearMonth

class MonthDialogFragment : DialogFragment() {

    private var _binding: FragmentMonthDialogBinding? = null
    private val binding get() = _binding!!

    // 로컬 상태 - 다이얼로그 내에서만 사용 (캘린더에 반영되지 않음)
    private var selectedYear: Int = YearMonth.now().year

    // 월 버튼 ID 리스트
    private val monthButtonIds = listOf(
        R.id.btn_january,
        R.id.btn_february,
        R.id.btn_march,
        R.id.btn_april,
        R.id.btn_may,
        R.id.btn_june,
        R.id.btn_july,
        R.id.btn_august,
        R.id.btn_september,
        R.id.btn_october,
        R.id.btn_november,
        R.id.btn_december
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 부모 프래그먼트에서 현재 년도 가져오기 (인자로 전달받거나)
        arguments?.getInt(ARG_INITIAL_YEAR)?.let { year ->
            selectedYear = year
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentMonthDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        dialog?.setCanceledOnTouchOutside(true)

        // 초기 년도 표시
        binding.tvDialogYear.text = selectedYear.toString()

        setupClickListeners()
    }

    /**
     * 클릭 리스너 설정
     */
    private fun setupClickListeners() {
        // 이전 년도 버튼 - 로컬 상태만 변경
        binding.btnPrevYear.setOnClickListener {
            selectedYear--
            binding.tvDialogYear.text = selectedYear.toString()
        }

        // 다음 년도 버튼 - 로컬 상태만 변경
        binding.btnNextYear.setOnClickListener {
            selectedYear++
            binding.tvDialogYear.text = selectedYear.toString()
        }

        // 월 선택 버튼들 (1월~12월)
        monthButtonIds.forEachIndexed { index, buttonId ->
            binding.root.findViewById<MaterialButton>(buttonId)?.setOnClickListener {
                // 다이얼로그 닫힌 후 처리를 위해 Fragment Result로 년/월 정보 전달
                setFragmentResult(
                    REQUEST_KEY_MONTH_SELECTED,
                    bundleOf(
                        BUNDLE_KEY_YEAR to selectedYear,
                        BUNDLE_KEY_MONTH to (index + 1)
                    )
                )
                removeScreenBlur()
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        removeScreenBlur()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY_MONTH_SELECTED = "month_selected_key"
        const val BUNDLE_KEY_YEAR = "year"
        const val BUNDLE_KEY_MONTH = "month"
        const val ARG_INITIAL_YEAR = "initial_year"

        fun newInstance(initialYear: Int): MonthDialogFragment {
            return MonthDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_INITIAL_YEAR, initialYear)
                }
            }
        }
    }
}
