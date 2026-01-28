package com.egobook.app.ui.diary.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.databinding.FragmentDiaryWriteBinding
import com.egobook.app.ui.diary.util.toDateTimeString
import com.egobook.app.ui.diary.util.toDayOfMonthString
import com.egobook.app.ui.diary.util.toMonthString
import com.egobook.app.ui.diary.util.toYearString
import com.egobook.app.ui.diary.viewmodel.DiaryWriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class DiaryWriteFragment : Fragment() {
    private var _binding: FragmentDiaryWriteBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DiaryWriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryWriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 하단 시스템 바 영역만큼 패딩을 주어 버튼이 가려지지 않게 함.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 기존 패딩은 유지하면서 하단만 시스템 바 높이만큼 추가
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        // 뒤로 가기 버튼 클릭 시 이전 화면(DiaryFragment)으로 이동
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        setupDiaryContentEditText()
        observeSelectedDate()
        observeContentState()
    }
    
    private fun setupDiaryContentEditText() {
        binding.etDiaryContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 ViewModel에 Event 전달
                viewModel.onEvent(DiaryWriteViewModel.ContentEvent.EnteredContent(s?.toString() ?: ""))
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun observeSelectedDate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedDate.collectLatest { date ->
                    // 날짜를 "2025년 12월 25일" 형식으로 표시
                    binding.tvYear.text = "${date.toYearString()}년 ${date.toMonthString()}월 ${date.toDayOfMonthString()}일"
                    
                    // 현재 시간을 "2025.12.25 17:32" 형식으로 표시
                    binding.tvInputTime.text = LocalDateTime.now().toDateTimeString()
                }
            }
        }
    }
    
    private fun observeContentState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contentState.collectLatest { state ->
                    // 글자수 표시 업데이트 (예: 0/400, 1/400, ...)
                    binding.tvCharCount.text = "${state.charCount}/${state.maxCharCount}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}