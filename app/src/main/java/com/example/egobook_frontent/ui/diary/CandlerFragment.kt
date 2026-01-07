package com.example.egobook_frontent.ui.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.example.egobook_frontent.databinding.FragmentCandlerBinding

class CandlerFragment : Fragment() {

    private var _binding: FragmentCandlerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCandlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 하단 시스템 바  영역만큼 패딩 주기
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 기존 패딩은 유지하면서 하단만 시스템 바 높이만큼 추가
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        //클릭 리스너
        binding.apply{
            // 리스트 버튼 클릭 시 이전 화면(DiaryFragment)으로 이동
            btnList.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
