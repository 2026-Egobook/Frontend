package com.egobook.app.ui.diary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentDiaryCheckBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.EmotionLevel
import com.egobook.app.ui.diary.util.toDateTimeString
import com.egobook.app.ui.diary.util.toDayOfMonthString
import com.egobook.app.ui.diary.util.toMonthString
import com.egobook.app.ui.diary.util.toYearString
import com.egobook.app.ui.diary.viewmodel.DiaryCheckViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiaryCheckFragment : Fragment() {

    private var _binding: FragmentDiaryCheckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryCheckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListener()
        observeDiary()
        observeDeleteSuccess()
    }

    private fun setClickListener() {
        binding.apply{
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnDelete.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)
                val dialog = DiaryDeleteDialogFragment()
                dialog.isCancelable = true
                
                // 삭제 확인 리스너 설정
                dialog.setOnDeleteConfirmListener(object : DiaryDeleteDialogFragment.OnDeleteConfirmListener {
                    override fun onDeleteConfirmed() {
                        // ViewModel의 deleteDiary 호출
                        viewModel.deleteDiary()
                    }
                })
                
                dialog.show(parentFragmentManager, "ConfirmDialog")
            }
        }
    }

    // ViewModel의 diary Flow를 직접 구독합니다.
    private fun observeDiary() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.diary.collectLatest { diary ->
                    if (diary != null) {
                        // diary 객체가 null이 아닐 때 UI를 업데이트합니다.
                        updateUi(diary)
                    } else {
                        // diary가 null이면 (데이터 로딩 실패 등) 사용자에게 알립니다.
                        Toast.makeText(requireContext(), "일기를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // UI를 업데이트하는 로직을 별도 함수로 분리
    private fun updateUi(diary: Diary) {
        binding.tvDiaryContent.text = diary.content
        binding.tvWrittenTime.text = diary.updatedAt.toDateTimeString()
        binding.tvDate.text = "${diary.createdAt.toYearString()}년 ${diary.createdAt.toMonthString()}월 ${diary.createdAt.toDayOfMonthString()}일"
        
        // 감정 레벨이 있으면 이미지 표시, 없으면 null
        if (diary.emotionLevel != null) {
            val emotionImageRes = getEmotionImageRes(diary.emotionLevel)
            binding.ivEmotion.setImageResource(emotionImageRes)
        } else {
            binding.ivEmotion.setImageDrawable(null) // 기본 이미지
        }
    }
    
    /**
     * Domain EmotionLevel을 UI 이미지 리소스로 변환
     */
    @DrawableRes
    private fun getEmotionImageRes(emotionLevel: EmotionLevel): Int {
        return when (emotionLevel) {
            EmotionLevel.VERY_BAD -> R.drawable.img_emotion_very_sad
            EmotionLevel.BAD -> R.drawable.img_emotion_sad
            EmotionLevel.NORMAL -> R.drawable.img_emotion_neutral
            EmotionLevel.GOOD -> R.drawable.img_emotion_happy
            EmotionLevel.VERY_GOOD -> R.drawable.img_emotion_very_happy
        }
    }

    // 삭제 성공 여부를 관찰
    private fun observeDeleteSuccess() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteSuccess.collectLatest { success ->
                    when (success) {
                        true -> {
                            Toast.makeText(requireContext(), "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        false -> {
                            Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        null -> {
                            // 초기 상태, 아무 작업 없음
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
