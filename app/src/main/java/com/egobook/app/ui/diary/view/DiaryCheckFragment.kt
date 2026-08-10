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
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.ui.util.toDateTimeString
import com.egobook.app.ui.util.toDayOfMonthString
import com.egobook.app.ui.util.toMonthString
import com.egobook.app.ui.util.toYearString
import com.egobook.app.ui.diary.viewmodel.DiaryCheckViewModel
import com.egobook.app.util.UiState
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
    
    override fun onResume() {
        super.onResume()
        // 화면이 다시 보여질 때마다 일기 데이터 새로고침 (수정 후 돌아왔을 때 반영)
        viewModel.refreshDiary()
    }

    private fun setClickListener() {
        binding.apply{
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnModify.setOnClickListener {
                val diaryState = viewModel.diaryState.value
                if(diaryState is UiState.Success) {
                    val currentDiary = diaryState.data
                    val action = DiaryCheckFragmentDirections
                        .actionDiaryCheckFragmentToDiaryWriteFragment(
                            selectedDate = currentDiary.date.toString(),
                            diaryId = currentDiary.diaryId
                        )
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "일기 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
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
                viewModel.diaryState.collectLatest { state ->
                    when (state) {
                        is UiState.Idle -> {
                            binding.pbLoading.visibility = View.GONE
                            binding.ivEmotion.visibility = View.GONE
                        }
                        is UiState.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.ivEmotion.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            updateUi(state.data)
                        }
                        is UiState.Failure -> {
                            binding.pbLoading.visibility = View.GONE
                            binding.ivEmotion.visibility = View.GONE
                            val message = state.message ?: "알 수 없는 오류가 발생했습니다."
                            Toast.makeText(requireContext(), 
                                "일기를 불러오는데 실패했습니다: $message", 
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // UI를 업데이트하는 로직을 별도 함수로 분리
    private fun updateUi(diary: Diary) {
        binding.tvDiaryContent.text = diary.content
        binding.tvWrittenTime.text = diary.writtenAt.toDateTimeString()
        binding.tvDate.text = "${diary.date.toYearString()}년 ${diary.date.toMonthString()}월 ${diary.date.toDayOfMonthString()}일"
        
        // 감정 레벨이 있으면 이미지 표시, 없으면 숨김
        if (diary.emotionLevel != null) {
            binding.ivEmotion.visibility = View.VISIBLE
            val emotionImageRes = getEmotionImageRes(diary.emotionLevel)
            binding.ivEmotion.setImageResource(emotionImageRes)
        } else {
            binding.ivEmotion.visibility = View.GONE
        }
        
        // 일기 타입 표시 (selector를 통해 선택된 타입만 하이라이트)
        setDiaryTypes(diary.types)
    }
    
    /**
     * 일기 타입에 따라 CardView의 선택 상태를 세팅
     */
    private fun setDiaryTypes(types: Set<DiaryType>) {
        binding.apply {
            cvEmotion.isSelected = DiaryType.EMOTION in types
            cvThought.isSelected = DiaryType.CONCERN in types
            cvPraise.isSelected = DiaryType.PRAISE in types
            cvGratitude.isSelected = DiaryType.GRATITUDE in types
        }
    }
    
    /**
     * 감정 레벨 (1~5)을 UI 이미지 리소스로 변환
     */
    @DrawableRes
    private fun getEmotionImageRes(emotionLevel: Int): Int {
        return when (emotionLevel) {
            1 -> R.drawable.img_emotion_very_sad
            2 -> R.drawable.img_emotion_sad
            3 -> R.drawable.img_emotion_neutral
            4 -> R.drawable.img_emotion_happy
            5 -> R.drawable.img_emotion_very_happy
            else -> R.drawable.img_emotion_neutral // 기본값
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
