package com.egobook.app.ui.diary.view

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentDiaryWriteBinding
import com.egobook.app.ui.diary.model.ToastMessage
import com.egobook.app.ui.diary.viewmodel.DiaryWriteViewModel
import com.egobook.app.ui.util.toDateTimeString
import com.egobook.app.ui.util.toDayOfMonthString
import com.egobook.app.ui.util.toMonthString
import com.egobook.app.ui.util.toYearString
import com.egobook.app.util.UiState
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
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
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 키보드

            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                maxOf(systemBars.bottom, ime.bottom)
            )
            
            // 키보드가 올라오면 ScrollView를 맨 아래로 스크롤
            if (ime.bottom > 0) {
                binding.svDiaryWrite.post {
                    binding.svDiaryWrite.fullScroll(View.FOCUS_DOWN)
                }
            }
            
            insets
        }

        // 뒤로 가기 버튼 클릭 시 이전 화면(DiaryFragment)으로 이동
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        // 저장하기 버튼 클릭 시 일기 저장
        binding.btnSave.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SaveDiary)
        }
        
        setupDiaryTypeCards()           // 일기 타입 카드뷰 클릭 리스너 설정
        setupEmotionLevelSelection()    // 감정 레벨 이미지 클릭 리스너 설정
        setupDiaryContentEditText()     // 일기 내용 입력 필드 설정 (글자수 제한, TextWatcher)
        observeSelectedDate()           // 선택된 날짜 관찰 및 UI 업데이트
        observeContentState()           // 컨텐츠 상태 관찰 (글자수, 감정 섹션, 저장 버튼 활성화)
        observeSaveResult()             // 저장 성공/실패 관찰
        observeDiaryLoadState()         // 수정 모드 데이터 로드 상태 관찰
    }
    
    private fun setupDiaryTypeCards() {
        // 감정 카드 클릭
        binding.cvEmotion.setOnClickListener {
            binding.cvEmotion.isSelected = !binding.cvEmotion.isSelected
            binding.tvEmotion.isSelected = binding.cvEmotion.isSelected
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.ToggleDiaryType("감정"))
        }
        
        // 고민 카드 클릭
        binding.cvWorry.setOnClickListener {
            binding.cvWorry.isSelected = !binding.cvWorry.isSelected
            binding.tvWorry.isSelected = binding.cvWorry.isSelected
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.ToggleDiaryType("고민"))
        }
        
        // 칭찬 카드 클릭
        binding.cvPraise.setOnClickListener {
            binding.cvPraise.isSelected = !binding.cvPraise.isSelected
            binding.tvPraise.isSelected = binding.cvPraise.isSelected
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.ToggleDiaryType("칭찬"))
        }
        
        // 감사 카드 클릭
        binding.cvThanks.setOnClickListener {
            binding.cvThanks.isSelected = !binding.cvThanks.isSelected
            binding.tvThanks.isSelected = binding.cvThanks.isSelected
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.ToggleDiaryType("감사"))
        }
    }
    
    private fun setupEmotionLevelSelection() {
        // 각 감정 이미지 클릭 리스너 설정
        binding.ivEmotion1.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SelectEmotionLevel(1))
        }
        binding.ivEmotion2.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SelectEmotionLevel(2))
        }
        binding.ivEmotion3.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SelectEmotionLevel(3))
        }
        binding.ivEmotion4.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SelectEmotionLevel(4))
        }
        binding.ivEmotion5.setOnClickListener {
            viewModel.onEvent(DiaryWriteViewModel.ContentEvent.SelectEmotionLevel(5))
        }
    }
    
    private fun setupDiaryContentEditText() {
        // 최대 글자 수 제한 (400자)
        binding.etDiaryContent.filters = arrayOf(InputFilter.LengthFilter(400))
        
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
                viewModel.selectedDate.collectLatest { selectedDate ->
                    // 날짜를 "2025년 12월 25일" 형식으로 표시
                    binding.tvYear.text = "${selectedDate.toYearString()}년 ${selectedDate.toMonthString()}월 ${selectedDate.toDayOfMonthString()}일"
                    
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
                    // 로딩 중이면 UI 업데이트 스킵 (프로그레스바 표시 중)
                    if (state.diaryLoadState is UiState.Loading) {
                        return@collectLatest
                    }

                    // 글자수 표시 업데이트 (예: 0/400, 1/400, ...)
                    binding.tvCharCount.text = "${state.charCount}/${state.maxCharCount}"
                    
                    // EditText 내용 업데이트 (수정 모드에서 초기 데이터 로드 시)
                    if (binding.etDiaryContent.text.toString() != state.content) {
                        binding.etDiaryContent.setText(state.content)
                        binding.etDiaryContent.setSelection(state.content.length) // 커서를 끝으로
                    }
                    
                    // 일기 타입 카드 선택 상태 업데이트
                    binding.cvEmotion.isSelected = state.selectedTypes.contains("감정")
                    binding.cvWorry.isSelected = state.selectedTypes.contains("고민")
                    binding.cvPraise.isSelected = state.selectedTypes.contains("칭찬")
                    binding.cvThanks.isSelected = state.selectedTypes.contains("감사")

                    // "감정" 타입이 선택되었을 때만 레벨 선택 섹션 표시
                    val isEmotionSelected = state.selectedTypes.contains("감정")
                    val visibility = if (isEmotionSelected) View.VISIBLE else View.GONE
                    
                    binding.tvHowIsYourFeeling.visibility = visibility
                    binding.stateLayout.visibility = visibility
                    binding.emotionLayout.visibility = visibility
                    
                    // 선택된 감정 레벨에 따라 이미지 업데이트
                    updateEmotionImages(state.selectedEmotionLevel)
                    
                    // 저장 버튼 활성화 상태 업데이트
                    binding.btnSave.isEnabled = state.isSaveButtonEnabled
                }
            }
        }
    }

    private fun observeDiaryLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contentState.collectLatest { state ->
                    when (val loadState = state.diaryLoadState) {
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            // 입력 UI 숨기기
                            binding.guideLayout.isVisible = false
                            binding.typeLayout.isVisible = false
                            binding.tvHowIsYourFeeling.isVisible = false
                            binding.stateLayout.isVisible = false
                            binding.emotionLayout.isVisible = false
                            binding.inputBoxLayout.isVisible = false
                            binding.btnSave.isVisible = false
                        }
                        is UiState.Success, is UiState.Idle -> {
                            binding.progressBar.isVisible = false
                            // 입력 UI 표시
                            binding.guideLayout.isVisible = true
                            binding.typeLayout.isVisible = true
                            // 감정 섹션은 선택 상태에 따라 표시 (observeContentState에서 처리)
                            binding.inputBoxLayout.isVisible = true
                            binding.btnSave.isVisible = true
                        }
                        is UiState.Failure -> {
                            binding.progressBar.isVisible = false
                            // 에러 처리 (필요시 토스트 또는 에러 UI 표시)
                            Toast.makeText(requireContext(), loadState.message ?: "데이터 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun updateEmotionImages(selectedLevel: Int) {
        // 모든 감정 이미지 업데이트
        updateEmotionImage(binding.ivEmotion1, 1, selectedLevel)
        updateEmotionImage(binding.ivEmotion2, 2, selectedLevel)
        updateEmotionImage(binding.ivEmotion3, 3, selectedLevel)
        updateEmotionImage(binding.ivEmotion4, 4, selectedLevel)
        updateEmotionImage(binding.ivEmotion5, 5, selectedLevel)
    }
    
    private fun updateEmotionImage(imageView: ShapeableImageView, level: Int, selectedLevel: Int) {
        val imageRes = if (level == selectedLevel) {
            getSelectedEmotionImage(level)
        } else {
            getUnselectedEmotionImage(level)
        }
        imageView.setImageResource(imageRes)
    }
    
    @DrawableRes
    private fun getSelectedEmotionImage(level: Int): Int {
        return when (level) {
            1 -> R.drawable.img_emotion_very_sad
            2 -> R.drawable.img_emotion_sad
            3 -> R.drawable.img_emotion_neutral
            4 -> R.drawable.img_emotion_happy
            5 -> R.drawable.img_emotion_very_happy
            else -> R.drawable.img_emotion_neutral
        }
    }
    
    @DrawableRes
    private fun getUnselectedEmotionImage(level: Int): Int {
        return when (level) {
            1 -> R.drawable.img_emotion_very_sad_unselectd // 오타 있는 파일명 그대로 사용
            2 -> R.drawable.img_emotion_sad_unselected
            3 -> R.drawable.img_emotion_neutral_unselected
            4 -> R.drawable.img_emotion_happy_unselected
            5 -> R.drawable.img_emotion_very_happy_unselected
            else -> R.drawable.img_emotion_neutral_unselected
        }
    }

    
    private fun observeSaveResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveResult.collectLatest { result ->
                    when (result) {
                        is DiaryWriteViewModel.SaveResult.Loading -> {
                            // 저장 중
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnSave.isEnabled = false
                        }
                        is DiaryWriteViewModel.SaveResult.Success -> {
                            // 저장 성공 -> 결과를 이전 화면(DiaryFragment)에 전달하고 이동
                            val messages = result.toastMessages
                            if (messages.isNotEmpty()) {
                                // SavedStateHandle로 토스트 메시지 전달 (더 안정적)
                                val jsonMessages = Gson().toJson(messages)
                                findNavController().previousBackStackEntry?.savedStateHandle?.set("toast_messages", jsonMessages)
                            }
                            findNavController().popBackStack()
                        }
                        is DiaryWriteViewModel.SaveResult.Error -> {
                            // 저장 실패
                            Toast.makeText(requireContext(), "일기 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}