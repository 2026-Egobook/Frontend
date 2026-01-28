package com.egobook.app.ui.diary.view

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentDiaryWriteBinding
import com.egobook.app.ui.diary.util.toDateTimeString
import com.egobook.app.ui.diary.util.toDayOfMonthString
import com.egobook.app.ui.diary.util.toMonthString
import com.egobook.app.ui.diary.util.toYearString
import com.egobook.app.ui.diary.viewmodel.DiaryWriteViewModel
import com.google.android.material.imageview.ShapeableImageView
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
        
        setupDiaryTypeCards()
        setupEmotionLevelSelection()
        setupDiaryContentEditText()
        observeSelectedDate()
        observeContentState()
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
                    
                    // "감정" 타입이 선택되었을 때만 레벨 선택 섹션 표시
                    val isEmotionSelected = state.selectedTypes.contains("감정")
                    val visibility = if (isEmotionSelected) View.VISIBLE else View.GONE
                    
                    binding.tvHowIsYourFeeling.visibility = visibility
                    binding.stateLayout.visibility = visibility
                    binding.emotionLayout.visibility = visibility
                    
                    // 선택된 감정 레벨에 따라 이미지 업데이트
                    updateEmotionImages(state.selectedEmotionLevel)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}