package com.egobook.app.ui.diary.view

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.egobook.app.databinding.FragmentDiaryDeleteDialogBinding
import com.egobook.app.removeScreenBlur

class DiaryDeleteDialogFragment : DialogFragment() {

    private var _binding: FragmentDiaryDeleteDialogBinding? = null
    private val binding get() = _binding!!

    // 삭제 확인 콜백 인터페이스
    interface OnDeleteConfirmListener {
        fun onDeleteConfirmed()
    }

    private var deleteConfirmListener: OnDeleteConfirmListener? = null

    fun setOnDeleteConfirmListener(listener: OnDeleteConfirmListener) {
        deleteConfirmListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentDiaryDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.apply{
            btnBack.setOnClickListener {
                removeScreenBlur()
                dismiss()
            }
            btnDelete.setOnClickListener {
                // 삭제 확인 콜백 호출
                deleteConfirmListener?.onDeleteConfirmed()
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
}
