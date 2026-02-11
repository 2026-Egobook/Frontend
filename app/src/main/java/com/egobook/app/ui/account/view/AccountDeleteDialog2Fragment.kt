package com.egobook.app.ui.account.view

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.egobook.app.databinding.FragmentAccountDeleteDialog2Binding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.ui.login.view.LoginActivity
import timber.log.Timber
import kotlin.getValue

class AccountDeleteDialog2Fragment : DialogFragment() {

    private var _binding: FragmentAccountDeleteDialog2Binding? = null
    private val binding get() = _binding!!

    //부모 프래그먼트의 뷰모델 공유
    private val viewModel: AccountViewModel by viewModels({ requireParentFragment() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentAccountDeleteDialog2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: 뷰 초기화 로직 추가
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        //백스택 제거 후 로그인 화면으로 이동
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        Timber.d("로그인 화면으로 이동")

        removeScreenBlur()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
